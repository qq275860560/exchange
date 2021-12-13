package com.ghf.exchange.boss.authorization.org.listener;

import com.ghf.exchange.boss.authorization.org.dto.GetOrgByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.org.dto.ListAncestorByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.org.dto.OrgRespDTO;
import com.ghf.exchange.boss.authorization.org.dto.TreeOrgReqDTO;
import com.ghf.exchange.boss.authorization.org.entity.Org;
import com.ghf.exchange.boss.authorization.org.event.UpdateFullOrgEvent;
import com.ghf.exchange.boss.authorization.org.event.UpdateOrgRoleEvent;
import com.ghf.exchange.boss.authorization.org.service.OrgService;
import com.ghf.exchange.boss.authorization.orgrole.dto.AddOrgRoleReqDTO;
import com.ghf.exchange.boss.authorization.orgrole.dto.GetOrgRoleByOrgnameAndRolenameReqDTO;
import com.ghf.exchange.boss.authorization.orgrole.dto.ListRoleByOrgnameReqDTO;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.util.JsonUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class OrgListener {

    @Lazy
    @Resource
    private OrgService orgService;

    @Lazy
    @Resource
    private OrgRoleService orgRoleService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Async
    @EventListener
    public void onUpdateOrgRoleEvent(UpdateOrgRoleEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("Org", "User", "UserOrg", "OrgRole");
        UpdateOrgRoleEvent.Payload payload = ((UpdateOrgRoleEvent.Payload) event.getSource());
        String orgname = payload.getOrgname();
        Set<String> rolenameSet = payload.getRolenameSet();

        ListRoleByOrgnameReqDTO listRoleByOrgnameReqDTO = new ListRoleByOrgnameReqDTO();
        listRoleByOrgnameReqDTO.setOrgname(orgname);
        Set<String> oldRolenameSet = orgRoleService.listRoleByOrgname(listRoleByOrgnameReqDTO).getData().stream().filter(e -> !ObjectUtils.isEmpty(e)).map(e -> e.getRolename()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toSet());
        Iterator<String> oldRolenameIterator = oldRolenameSet.iterator();
        while (oldRolenameIterator.hasNext()) {
            String oldRolename = oldRolenameIterator.next();
            //本来存在，现在不存在，相当于执行禁用操作
            if (!rolenameSet.contains(oldRolename)) {
                oldRolenameIterator.remove();
                GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO = new GetOrgRoleByOrgnameAndRolenameReqDTO();
                getOrgRoleByOrgnameAndRolenameReqDTO.setRolename(oldRolename);
                getOrgRoleByOrgnameAndRolenameReqDTO.setOrgname(orgname);
                orgRoleService.disableOrgRole(getOrgRoleByOrgnameAndRolenameReqDTO);
            }
        }
        Iterator<String> rolenameIterator = rolenameSet.iterator();
        while (rolenameIterator.hasNext()) {
            String rolename = rolenameIterator.next();
            //本来不存在，现在存在，相当于执行新增操作或启用操作
            if (!oldRolenameSet.contains(rolename)) {
                GetOrgRoleByOrgnameAndRolenameReqDTO getOrgRoleByOrgnameAndRolenameReqDTO = new GetOrgRoleByOrgnameAndRolenameReqDTO();
                getOrgRoleByOrgnameAndRolenameReqDTO.setRolename(rolename);
                getOrgRoleByOrgnameAndRolenameReqDTO.setOrgname(orgname);
                boolean b = orgRoleService.existsOrgRoleByOrgnameAndRolename(getOrgRoleByOrgnameAndRolenameReqDTO).getData();
                if (b) {
                    orgRoleService.enableOrgRole(getOrgRoleByOrgnameAndRolenameReqDTO);
                } else {
                    AddOrgRoleReqDTO addOrgRoleReqDTO = new AddOrgRoleReqDTO();
                    addOrgRoleReqDTO.setOrgname(orgname);
                    addOrgRoleReqDTO.setRolename(rolename);
                    orgRoleService.addOrgRole(addOrgRoleReqDTO);
                }
            }
        }
    }

    @Async
    @EventListener
    public void onUpdateFullOrg(UpdateFullOrgEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("Org", "User", "UserOrg", "OrgRole");
        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = ((GetOrgByOrgnameReqDTO) event.getSource());
        String orgname = getOrgByOrgnameReqDTO.getOrgname();
        updateFullorg(orgname);
    }

    private void updateFullorg(String orgname) {
        TreeOrgReqDTO treeOrgReqDTO = new TreeOrgReqDTO();
        treeOrgReqDTO.setOrgname(orgname);
        OrgRespDTO orgRespDTO = orgService.treeOrg(treeOrgReqDTO).getData();

        //重新初始化完整组织id,完整地区组织名称(编码),完整地区组织名称(描述)

        StringBuilder fullOrgIdStringBuilder = new StringBuilder(",");
        StringBuilder fullOrgnameStringBuilder = new StringBuilder(",");
        StringBuilder fullOrgdescStringBuilder = new StringBuilder(",");
        ListAncestorByOrgnameReqDTO listAncestorByOrgnameReqDTO = new ListAncestorByOrgnameReqDTO();
        listAncestorByOrgnameReqDTO.setOrgname(orgRespDTO.getParentOrgname());
        orgService.listAncestorByOrgname(listAncestorByOrgnameReqDTO).getData().forEach(e -> {
            fullOrgIdStringBuilder.append(e.getId()).append(",");
            fullOrgnameStringBuilder.append(e.getOrgname()).append(",");
            fullOrgdescStringBuilder.append(e.getOrgdesc()).append(",");
        });
        fullOrgIdStringBuilder.append(orgRespDTO.getId()).append(",");
        fullOrgnameStringBuilder.append(orgRespDTO.getOrgname()).append(",");
        fullOrgdescStringBuilder.append(orgRespDTO.getOrgdesc()).append(",");

        OrgRespDTO afterOrgRespDTO = orgService.getOrgByOrgname(GetOrgByOrgnameReqDTO.builder().orgname(orgRespDTO.getOrgname()).build()).getData();
        Org org = ModelMapperUtil.map(afterOrgRespDTO, Org.class);
        org.setFullOrgId(fullOrgIdStringBuilder.toString());
        org.setFullOrgname(fullOrgnameStringBuilder.toString());
        org.setFullOrgdesc(fullOrgdescStringBuilder.toString());
        org.setDeep(org.getFullOrgname().split(",").length - 1);
        orgService.update(org);
        clearRedisService.clearPrefixs("Org");

        //递归
        orgRespDTO.getChildren().forEach(e -> updateFullorg(e.getOrgname()));

    }

}