package com.ghf.exchange.boss.common.area.listener;

import com.ghf.exchange.boss.common.area.dto.AreaRespDTO;
import com.ghf.exchange.boss.common.area.dto.GetAreaByAreanameReqDTO;
import com.ghf.exchange.boss.common.area.dto.ListAncestorByAreanameReqDTO;
import com.ghf.exchange.boss.common.area.dto.TreeAreaReqDTO;
import com.ghf.exchange.boss.common.area.entity.Area;
import com.ghf.exchange.boss.common.area.event.UpdateFullAreaEvent;
import com.ghf.exchange.boss.common.area.service.AreaService;
import com.ghf.exchange.config.ClearRedisConfig;
import com.ghf.exchange.util.JsonUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class AreaListener {

    @Lazy
    @Resource
    private AreaService areaService;

    @Lazy
    @Resource
    private ClearRedisConfig clearRedisService;

    @Async
    @EventListener
    public void onUpdateFullarea(UpdateFullAreaEvent event) {
        log.info("接收到消息={}", JsonUtil.toJsonString(event.getSource()));
        clearRedisService.clearPrefixs("Area");
        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO = ((GetAreaByAreanameReqDTO) event.getSource());
        String areaname = getAreaByAreanameReqDTO.getAreaname();
        updateFullarea(areaname);
    }

    private void updateFullarea(String areaname) {
        TreeAreaReqDTO treeAreaReqDTO = new TreeAreaReqDTO();
        treeAreaReqDTO.setAreaname(areaname);
        clearRedisService.clearPrefixs("Area");
        AreaRespDTO areaRespDTO = areaService.treeArea(treeAreaReqDTO).getData();

        //重新初始化完整地区id,完整地区英文名称(编码),完整地区中文名称(描述)

        StringBuilder fullAreaIdStringBuilder = new StringBuilder(",");
        StringBuilder fullAreanameStringBuilder = new StringBuilder(",");
        StringBuilder fullAreadescStringBuilder = new StringBuilder(",");
        ListAncestorByAreanameReqDTO listAncestorByAreanameReqDTO = new ListAncestorByAreanameReqDTO();
        listAncestorByAreanameReqDTO.setAreaname(areaRespDTO.getParentAreaname());
        areaService.listAncestorByAreaname(listAncestorByAreanameReqDTO).getData().forEach(e -> {
            fullAreaIdStringBuilder.append(e.getId()).append(",");
            fullAreanameStringBuilder.append(e.getAreaname()).append(",");
            fullAreadescStringBuilder.append(e.getAreadesc()).append(",");
        });
        fullAreaIdStringBuilder.append(areaRespDTO.getId()).append(",");
        fullAreanameStringBuilder.append(areaRespDTO.getAreaname()).append(",");
        fullAreadescStringBuilder.append(areaRespDTO.getAreadesc()).append(",");

        AreaRespDTO afterAreaRespDTO = areaService.getAreaByAreaname(GetAreaByAreanameReqDTO.builder().areaname(areaRespDTO.getAreaname()).build()).getData();
        Area area = ModelMapperUtil.map(afterAreaRespDTO, Area.class);
        area.setFullAreaId(fullAreaIdStringBuilder.toString());
        area.setFullAreaname(fullAreanameStringBuilder.toString());
        area.setFullAreadesc(fullAreadescStringBuilder.toString());
        area.setDeep(area.getFullAreaname().split(",").length - 1);
        areaService.update(area);
        clearRedisService.clearPrefixs("Area");

        //递归
        areaRespDTO.getChildren().forEach(e -> updateFullarea(e.getAreaname()));

    }
}