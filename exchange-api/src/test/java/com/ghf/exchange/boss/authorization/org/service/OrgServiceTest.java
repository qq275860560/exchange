
package com.ghf.exchange.boss.authorization.org.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.authorization.org.dto.*;
import com.ghf.exchange.boss.authorization.org.entity.Org;
import com.ghf.exchange.boss.authorization.org.entity.QOrg;
import com.ghf.exchange.boss.authorization.orgrole.entity.QOrgRole;
import com.ghf.exchange.boss.authorization.orgrole.service.OrgRoleService;
import com.ghf.exchange.boss.common.area.dto.AreaRespDTO;
import com.ghf.exchange.boss.common.area.dto.GetAreaByAreanameReqDTO;
import com.ghf.exchange.boss.common.area.service.AreaService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class OrgServiceTest {
    @Lazy
    @Resource
    private OrgService orgService;

    @Lazy
    @Resource
    private OrgRoleService orgRoleService;

    @Lazy
    @Resource
    private AreaService areaService;
    @Lazy
    @Resource
    private UserService userService;

    /**
     * 平台管理员账号（测试）
     */
    public static final String ADMIN_USER_NAME = "admin";

    /**
     * 平台管理员密码（测试）
     */
    public static final String ADMIN_PASSWORD = "123456";

    @SneakyThrows
    @Test
    public void addOrg() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddOrgReqDTO addOrgReqDTO = new AddOrgReqDTO();
        addOrgReqDTO.setOrgname("test-addOrg-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        addOrgReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_PROVINCE");
            add("ROLE_CITY");
        }});
        orgService.addOrg(addOrgReqDTO);

        TimeUnit.SECONDS.sleep(3);

        String orgname = addOrgReqDTO.getOrgname();

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrg.org.orgname.eq(orgname));
        Org org = orgService.get(predicate);
        if (ObjectUtils.isEmpty(org.getRolenames())) {
            org.setRolenames(",");
            org.setRoledescs(",");
        }

        Set<String> rolenameSet2 = Arrays.asList(org.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());
        Set<String> roledescSet2 = Arrays.asList(org.getRoledescs().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(orgname);
        OrgRespDTO orgOutput = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();

        Set<String> rolenameSet = orgOutput.getRolenameSet();
        Set<String> roledescSet = orgOutput.getRoledescSet();

        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));
        Assert.assertTrue(isSetEqual(roledescSet, roledescSet2));

        //清理
        orgService.delete(QOrg.org.orgname.eq(orgname));

        orgRoleService.getJpaQueryFactory().delete(QOrgRole.orgRole).where(QOrgRole.orgRole.orgname.eq(addOrgReqDTO.getOrgname()));

    }

    public static boolean isSetEqual(Set set1, Set set2) {

        if (set1 == null && set2 == null) {
            return true; // Both are null
        }

        if (ObjectUtils.isEmpty(set1) && ObjectUtils.isEmpty(set2)) {
            return true;
        }
        if (set1 == null || set2 == null || set1.size() != set2.size()
                || set1.size() == 0 || set2.size() == 0) {
            return false;
        }

        Iterator ite1 = set1.iterator();
        Iterator ite2 = set2.iterator();

        boolean isFullEqual = true;

        while (ite2.hasNext()) {
            if (!set1.contains(ite2.next())) {
                isFullEqual = false;
            }
        }

        return isFullEqual;
    }

    @SneakyThrows
    @Test
    public void updateOrg() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddOrgReqDTO addOrgReqDTO = new AddOrgReqDTO();
        addOrgReqDTO.setOrgname("test-updateOrg-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        addOrgReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_PROVINCE");
            add("ROLE_CITY");
        }});
        orgService.addOrg(addOrgReqDTO);

        TimeUnit.SECONDS.sleep(3);

        String orgname = addOrgReqDTO.getOrgname();

        UpdateOrgByOrgnameReqDTO updateOrgReqDTO = ModelMapperUtil.map(orgService.get(QOrg.org.orgname.eq(orgname)), UpdateOrgByOrgnameReqDTO.class);
        updateOrgReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_PROVINCE");
        }});

        orgService.updateOrgByOrgname(updateOrgReqDTO);

        TimeUnit.SECONDS.sleep(20);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrg.org.orgname.eq(orgname));
        Org org = orgService.get(predicate);
        if (ObjectUtils.isEmpty(org.getRolenames())) {
            org.setRolenames(",");
        }

        Set<String> rolenameSet2 = Arrays.asList(org.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(orgname);
        OrgRespDTO orgOutput = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();

        Set<String> rolenameSet = orgOutput.getRolenameSet();

        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));

        //清理
        orgService.delete(QOrg.org.orgname.eq(orgname));

        orgRoleService.getJpaQueryFactory().delete(QOrgRole.orgRole).where(QOrgRole.orgRole.orgname.eq(addOrgReqDTO.getOrgname()));

    }

    @SneakyThrows
    @Test
    public void updateOrg2() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddOrgReqDTO addOrgReqDTO = new AddOrgReqDTO();
        addOrgReqDTO.setOrgname("test-updateOrg2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        addOrgReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_PROVINCE");
            add("ROLE_CITY");
        }});
        orgService.addOrg(addOrgReqDTO);

        TimeUnit.SECONDS.sleep(3);

        String orgname = addOrgReqDTO.getOrgname();

        UpdateOrgByOrgnameReqDTO updateOrgReqDTO = ModelMapperUtil.map(orgService.get(QOrg.org.orgname.eq(orgname)), UpdateOrgByOrgnameReqDTO.class);
        updateOrgReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_PROVINCE");
            add("ROLE_CITY");
        }});

        orgService.updateOrgByOrgname(updateOrgReqDTO);

        TimeUnit.SECONDS.sleep(20);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrg.org.orgname.eq(orgname));
        Org org = orgService.get(predicate);
        if (ObjectUtils.isEmpty(org.getRolenames())) {
            org.setRolenames(",");
        }

        Set<String> rolenameSet2 = Arrays.asList(org.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(orgname);
        OrgRespDTO orgOutput = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();

        Set<String> rolenameSet = orgOutput.getRolenameSet();

        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));

        //清理
        orgService.delete(QOrg.org.orgname.eq(orgname));

        orgRoleService.getJpaQueryFactory().delete(QOrgRole.orgRole).where(QOrgRole.orgRole.orgname.eq(addOrgReqDTO.getOrgname()));

    }

    @SneakyThrows
    @Test
    public void updateOrg3() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddOrgReqDTO addOrgReqDTO = new AddOrgReqDTO();
        addOrgReqDTO.setOrgname("test-updateOrg3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        addOrgReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_PROVINCE");

        }});
        orgService.addOrg(addOrgReqDTO);

        TimeUnit.SECONDS.sleep(3);

        String orgname = addOrgReqDTO.getOrgname();

        UpdateOrgByOrgnameReqDTO updateOrgReqDTO = ModelMapperUtil.map(orgService.get(QOrg.org.orgname.eq(orgname)), UpdateOrgByOrgnameReqDTO.class);
        updateOrgReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_PROVINCE");
            add("ROLE_CITY");
        }});

        orgService.updateOrgByOrgname(updateOrgReqDTO);

        TimeUnit.SECONDS.sleep(20);

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QOrg.org.orgname.eq(orgname));
        Org org = orgService.get(predicate);
        if (ObjectUtils.isEmpty(org.getRolenames())) {
            org.setRolenames(",");
        }

        Set<String> rolenameSet2 = Arrays.asList(org.getRolenames().split(","))
                .stream()
                .filter(e -> !ObjectUtils.isEmpty(e))
                .collect(Collectors.toSet());

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(orgname);
        OrgRespDTO orgOutput = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();

        Set<String> rolenameSet = orgOutput.getRolenameSet();

        Assert.assertTrue(isSetEqual(rolenameSet, rolenameSet2));

        //清理
        orgService.delete(QOrg.org.orgname.eq(orgname));

        orgRoleService.getJpaQueryFactory().delete(QOrgRole.orgRole).where(QOrgRole.orgRole.orgname.eq(addOrgReqDTO.getOrgname()));

    }

    @SneakyThrows
    @Test
    public void pageOrg() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddOrgReqDTO addOrgReqDTO = new AddOrgReqDTO();
        addOrgReqDTO.setOrgname("test-pageOrg-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        addOrgReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_PROVINCE");
            add("ROLE_CITY");
        }});
        orgService.addOrg(addOrgReqDTO);

        PageOrgReqDTO pageOrgReqDTO = new PageOrgReqDTO();
        Result<PageRespDTO<OrgRespDTO>> result = orgService.pageOrg(pageOrgReqDTO);

        Assert.assertTrue(result.getData().getTotal() > 0);

        //清理
        String orgname = addOrgReqDTO.getOrgname();
        orgService.delete(QOrg.org.orgname.eq(orgname));

        orgRoleService.getJpaQueryFactory().delete(QOrgRole.orgRole).where(QOrgRole.orgRole.orgname.eq(addOrgReqDTO.getOrgname()));

    }

    @SneakyThrows
    @Test
    public void addOrg6() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddOrgReqDTO addOrgReqDTO = new AddOrgReqDTO();
        addOrgReqDTO.setOrgname("test-addOrg-orgname1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO.setOrgdesc("test-addOrg-orgdesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        orgService.addOrg(addOrgReqDTO);
        TimeUnit.SECONDS.sleep(3);

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(addOrgReqDTO.getOrgname());
        OrgRespDTO targeOutput = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        log.info("targeOutput.getFullOrgname()={}", targeOutput.getFullOrgname());
        Assert.assertTrue(targeOutput.getFullOrgname().equals("," + addOrgReqDTO.getOrgname() + ","));

        AddOrgReqDTO addOrgReqDTO2 = new AddOrgReqDTO();
        addOrgReqDTO2.setOrgname("test-addOrg-orgname2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO2.setOrgdesc("test-addOrg-orgdesc2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO2.setParentOrgname(addOrgReqDTO.getOrgname());
        orgService.addOrg(addOrgReqDTO2);
        TimeUnit.SECONDS.sleep(3);

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO2 = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO2.setOrgname(addOrgReqDTO2.getOrgname());
        OrgRespDTO targeOutput2 = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO2).getData();
        log.info("targeOutput2.getFullOrgname()()={}", targeOutput2.getFullOrgname());
        Assert.assertTrue(targeOutput2.getFullOrgname().equals("," + addOrgReqDTO.getOrgname() + "," + addOrgReqDTO2.getOrgname() + ","));

        AddOrgReqDTO addOrgReqDTO3 = new AddOrgReqDTO();
        addOrgReqDTO3.setOrgname("test-addOrg-orgname3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO3.setOrgdesc("test-addOrg-orgdesc3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO3.setParentOrgname(addOrgReqDTO2.getOrgname());
        orgService.addOrg(addOrgReqDTO3);
        TimeUnit.SECONDS.sleep(3);

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO3 = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO3.setOrgname(addOrgReqDTO3.getOrgname());
        OrgRespDTO targeOutput3 = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO3).getData();
        log.info("targeOutput3.getFullOrgname()={}", targeOutput3.getFullOrgname());
        Assert.assertTrue(targeOutput3.getFullOrgname().equals("," + addOrgReqDTO.getOrgname() + "," + addOrgReqDTO2.getOrgname() + "," + addOrgReqDTO3.getOrgname() + ","));

        orgService.delete(QOrg.org.orgname.eq(addOrgReqDTO.getOrgname()));
        orgService.delete(QOrg.org.orgname.eq(addOrgReqDTO2.getOrgname()));
        orgService.delete(QOrg.org.orgname.eq(addOrgReqDTO3.getOrgname()));
    }

    @SneakyThrows
    @Test
    public void updateOrg6() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddOrgReqDTO addOrgReqDTO = new AddOrgReqDTO();
        addOrgReqDTO.setOrgname("test-addOrg-orgname1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO.setOrgdesc("test-addOrg-orgdesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        orgService.addOrg(addOrgReqDTO);
        TimeUnit.SECONDS.sleep(3);

        AddOrgReqDTO addOrgReqDTO2 = new AddOrgReqDTO();
        addOrgReqDTO2.setOrgname("test-addOrg-orgname2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO2.setOrgdesc("test-addOrg-orgdesc2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO2.setParentOrgname(addOrgReqDTO.getOrgname());
        orgService.addOrg(addOrgReqDTO2);
        TimeUnit.SECONDS.sleep(3);

        AddOrgReqDTO addOrgReqDTO3 = new AddOrgReqDTO();
        addOrgReqDTO3.setOrgname("test-addOrg-orgname3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO3.setOrgdesc("test-addOrg-orgdesc3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addOrgReqDTO3.setParentOrgname(addOrgReqDTO2.getOrgname());
        orgService.addOrg(addOrgReqDTO3);
        TimeUnit.SECONDS.sleep(3);

        UpdateOrgByOrgnameReqDTO updateOrgByOrgnameReqDTO = new UpdateOrgByOrgnameReqDTO();
        updateOrgByOrgnameReqDTO.setOrgname(addOrgReqDTO.getOrgname());
        updateOrgByOrgnameReqDTO.setOrgdesc("test-updateOrg-orgdesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        orgService.updateOrgByOrgname(updateOrgByOrgnameReqDTO);
        TimeUnit.SECONDS.sleep(3);

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(addOrgReqDTO.getOrgname());
        OrgRespDTO targeOutput = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();
        log.info("targeOutput.getFullOrgdesc()={}", targeOutput.getFullOrgdesc());
        Assert.assertTrue(targeOutput.getFullOrgdesc().equals("," + updateOrgByOrgnameReqDTO.getOrgdesc() + ","));

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO2 = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO2.setOrgname(addOrgReqDTO2.getOrgname());
        OrgRespDTO targeOutput2 = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO2).getData();
        log.info("targeOutput2.getFullOrgdesc()={}", targeOutput2.getFullOrgdesc());
        Assert.assertTrue(targeOutput2.getFullOrgdesc().equals("," + updateOrgByOrgnameReqDTO.getOrgdesc() + "," + addOrgReqDTO2.getOrgdesc() + ","));

        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO3 = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO3.setOrgname(addOrgReqDTO3.getOrgname());
        OrgRespDTO targeOutput3 = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO3).getData();
        log.info("targeOutput3.getFullOrgdesc()={}", targeOutput3.getFullOrgdesc());
        Assert.assertTrue(targeOutput3.getFullOrgdesc().equals("," + updateOrgByOrgnameReqDTO.getOrgdesc() + "," + addOrgReqDTO2.getOrgdesc() + "," + addOrgReqDTO3.getOrgdesc() + ","));

        orgService.delete(QOrg.org.orgname.eq(addOrgReqDTO.getOrgname()));
        orgService.delete(QOrg.org.orgname.eq(addOrgReqDTO2.getOrgname()));
        orgService.delete(QOrg.org.orgname.eq(addOrgReqDTO3.getOrgname()));
    }

    @SneakyThrows
    //@Test
    public void batchUpdateOrg() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        String orgname = "ORG_SGSL";
        GetOrgByOrgnameReqDTO getOrgByOrgnameReqDTO = new GetOrgByOrgnameReqDTO();
        getOrgByOrgnameReqDTO.setOrgname(orgname);
        OrgRespDTO orgOutput = orgService.getOrgByOrgname(getOrgByOrgnameReqDTO).getData();

        UpdateOrgByOrgnameReqDTO updateOrgByOrgnameReqDTO = ModelMapperUtil.map(orgOutput, UpdateOrgByOrgnameReqDTO.class);
        orgService.updateOrgByOrgname(updateOrgByOrgnameReqDTO);
        TimeUnit.SECONDS.sleep(720);

        Assert.assertTrue(1 == 1);

    }

    @SneakyThrows
    @Test
    public void treeOrg() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddOrgReqDTO addOrgReqDTO = new AddOrgReqDTO();
        addOrgReqDTO.setOrgname("test-addOrg-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        addOrgReqDTO.setRolenameSet(new HashSet<String>() {{
            add("ROLE_PROVINCE");
            add("ROLE_CITY");
        }});
        orgService.addOrg(addOrgReqDTO);

        String orgname = "";
        TreeOrgReqDTO treeOrgReqDTO = new TreeOrgReqDTO();
        treeOrgReqDTO.setOrgname(orgname);
        OrgRespDTO orgOutput = orgService.treeOrg(treeOrgReqDTO).getData();

        Assert.assertTrue(!ObjectUtils.isEmpty(orgOutput.getOrgname()));

    }

    @SneakyThrows
    //@Test
    public void batchUpdateOrg2() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        BooleanBuilder predicate = new BooleanBuilder();
        //此接口只能获取启用状态的

        List<Org> originList = orgService.list(predicate);
        for (Org org : originList) {
            if (!ObjectUtils.isEmpty(org.getAreaname())) {
                AreaRespDTO areaOutput = areaService.getAreaByAreaname(GetAreaByAreanameReqDTO.builder().areaname(org.getAreaname()).build()).getData();
                org.setAreadesc(areaOutput.getAreadesc());
                org.setFullAreaname(areaOutput.getFullAreaname());
                org.setFullAreadesc(areaOutput.getFullAreadesc());
                orgService.update(org);
            }
        }

        TimeUnit.SECONDS.sleep(720);

        Assert.assertTrue(1 == 1);

    }

}
