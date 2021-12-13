
package com.ghf.exchange.boss.common.area.service;

import com.ghf.exchange.Application;
import com.ghf.exchange.boss.authorication.user.dto.LoginReqDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.common.area.dto.*;
import com.ghf.exchange.boss.common.area.entity.QArea;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.util.ModelMapperUtil;
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
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class AreaServiceTest {
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
    public void addArea() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddAreaReqDTO addAreaReqDTO = new AddAreaReqDTO();
        addAreaReqDTO.setAreaname("test-addArea-areaname1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO.setAreadesc("test-addArea-areadesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        areaService.addArea(addAreaReqDTO);
        TimeUnit.SECONDS.sleep(3);

        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO = new GetAreaByAreanameReqDTO();
        getAreaByAreanameReqDTO.setAreaname(addAreaReqDTO.getAreaname());
        AreaRespDTO targeOutput = areaService.getAreaByAreaname(getAreaByAreanameReqDTO).getData();
        Assert.assertTrue(targeOutput.getFullAreaname().equals("," + addAreaReqDTO.getAreaname() + ","));

        AddAreaReqDTO addAreaReqDTO2 = new AddAreaReqDTO();
        addAreaReqDTO2.setAreaname("test-addArea-areaname2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO2.setAreadesc("test-addArea-areadesc2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO2.setParentAreaname(addAreaReqDTO.getAreaname());
        areaService.addArea(addAreaReqDTO2);
        TimeUnit.SECONDS.sleep(3);

        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO2 = new GetAreaByAreanameReqDTO();
        getAreaByAreanameReqDTO2.setAreaname(addAreaReqDTO2.getAreaname());
        AreaRespDTO targeOutput2 = areaService.getAreaByAreaname(getAreaByAreanameReqDTO2).getData();
        Assert.assertTrue(targeOutput2.getFullAreaname().equals("," + addAreaReqDTO.getAreaname() + "," + addAreaReqDTO2.getAreaname() + ","));

        AddAreaReqDTO addAreaReqDTO3 = new AddAreaReqDTO();
        addAreaReqDTO3.setAreaname("test-addArea-areaname3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO3.setAreadesc("test-addArea-areadesc3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO3.setParentAreaname(addAreaReqDTO2.getAreaname());
        areaService.addArea(addAreaReqDTO3);
        TimeUnit.SECONDS.sleep(3);

        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO3 = new GetAreaByAreanameReqDTO();
        getAreaByAreanameReqDTO3.setAreaname(addAreaReqDTO3.getAreaname());
        AreaRespDTO targeOutput3 = areaService.getAreaByAreaname(getAreaByAreanameReqDTO3).getData();
        Assert.assertTrue(targeOutput3.getFullAreaname().equals("," + addAreaReqDTO.getAreaname() + "," + addAreaReqDTO2.getAreaname() + "," + addAreaReqDTO3.getAreaname() + ","));

        areaService.delete(QArea.area.areaname.eq(addAreaReqDTO.getAreaname()));
        areaService.delete(QArea.area.areaname.eq(addAreaReqDTO2.getAreaname()));
        areaService.delete(QArea.area.areaname.eq(addAreaReqDTO3.getAreaname()));
    }

    @SneakyThrows
    @Test
    public void updateArea() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddAreaReqDTO addAreaReqDTO = new AddAreaReqDTO();
        addAreaReqDTO.setAreaname("test-addArea-areaname1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO.setAreadesc("test-addArea-areadesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        areaService.addArea(addAreaReqDTO);
        TimeUnit.SECONDS.sleep(3);

        AddAreaReqDTO addAreaReqDTO2 = new AddAreaReqDTO();
        addAreaReqDTO2.setAreaname("test-addArea-areaname2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO2.setAreadesc("test-addArea-areadesc2-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO2.setParentAreaname(addAreaReqDTO.getAreaname());
        areaService.addArea(addAreaReqDTO2);
        TimeUnit.SECONDS.sleep(3);

        AddAreaReqDTO addAreaReqDTO3 = new AddAreaReqDTO();
        addAreaReqDTO3.setAreaname("test-addArea-areaname3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO3.setAreadesc("test-addArea-areadesc3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        addAreaReqDTO3.setParentAreaname(addAreaReqDTO2.getAreaname());
        areaService.addArea(addAreaReqDTO3);
        TimeUnit.SECONDS.sleep(3);

        UpdateAreaByAreanameReqDTO updateAreaByAreanameReqDTO = new UpdateAreaByAreanameReqDTO();
        updateAreaByAreanameReqDTO.setAreaname(addAreaReqDTO.getAreaname());
        updateAreaByAreanameReqDTO.setAreadesc("test-updateArea-areadesc1-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        areaService.updateAreaByAreaname(updateAreaByAreanameReqDTO);
        TimeUnit.SECONDS.sleep(3);

        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO = new GetAreaByAreanameReqDTO();
        getAreaByAreanameReqDTO.setAreaname(addAreaReqDTO.getAreaname());
        AreaRespDTO targeOutput = areaService.getAreaByAreaname(getAreaByAreanameReqDTO).getData();
        Assert.assertTrue(targeOutput.getFullAreadesc().equals("," + updateAreaByAreanameReqDTO.getAreadesc() + ","));

        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO2 = new GetAreaByAreanameReqDTO();
        getAreaByAreanameReqDTO2.setAreaname(addAreaReqDTO2.getAreaname());
        AreaRespDTO targeOutput2 = areaService.getAreaByAreaname(getAreaByAreanameReqDTO2).getData();
        Assert.assertTrue(targeOutput2.getFullAreadesc().equals("," + updateAreaByAreanameReqDTO.getAreadesc() + "," + addAreaReqDTO2.getAreadesc() + ","));

        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO3 = new GetAreaByAreanameReqDTO();
        getAreaByAreanameReqDTO3.setAreaname(addAreaReqDTO3.getAreaname());
        AreaRespDTO targeOutput3 = areaService.getAreaByAreaname(getAreaByAreanameReqDTO3).getData();
        Assert.assertTrue(targeOutput3.getFullAreadesc().equals("," + updateAreaByAreanameReqDTO.getAreadesc() + "," + addAreaReqDTO2.getAreadesc() + "," + addAreaReqDTO3.getAreadesc() + ","));

        areaService.delete(QArea.area.areaname.eq(addAreaReqDTO.getAreaname()));
        areaService.delete(QArea.area.areaname.eq(addAreaReqDTO2.getAreaname()));
        areaService.delete(QArea.area.areaname.eq(addAreaReqDTO3.getAreaname()));
    }

    @SneakyThrows
    @Test
    public void pageArea() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddAreaReqDTO addAreaReqDTO = new AddAreaReqDTO();
        addAreaReqDTO.setAreaname("test-pageArea-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        areaService.addArea(addAreaReqDTO);
        TimeUnit.SECONDS.sleep(3);
        PageAreaReqDTO pageAreaReqDTO = new PageAreaReqDTO();
        Result<PageRespDTO<AreaRespDTO>> result = areaService.pageArea(pageAreaReqDTO);

        Assert.assertTrue(result.getData().getTotal() > 0);

        //清理
        String areaname = addAreaReqDTO.getAreaname();
        areaService.delete(QArea.area.areaname.eq(areaname));

    }

    @SneakyThrows
    //@Test
    public void batchUpdateArea() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        String areaname = "440000000000";
        GetAreaByAreanameReqDTO getAreaByAreanameReqDTO = new GetAreaByAreanameReqDTO();
        getAreaByAreanameReqDTO.setAreaname(areaname);
        AreaRespDTO areaOutput = areaService.getAreaByAreaname(getAreaByAreanameReqDTO).getData();

        UpdateAreaByAreanameReqDTO updateAreaByAreanameReqDTO = ModelMapperUtil.map(areaOutput, UpdateAreaByAreanameReqDTO.class);
        areaService.updateAreaByAreaname(updateAreaByAreanameReqDTO);
        TimeUnit.SECONDS.sleep(720);

        Assert.assertTrue(1 == 1);

    }

    @SneakyThrows
    @Test
    public void treeArea() {
        userService.login(LoginReqDTO.builder().username(ADMIN_USER_NAME).password(ADMIN_PASSWORD).build());

        AddAreaReqDTO addAreaReqDTO = new AddAreaReqDTO();
        addAreaReqDTO.setAreaname("test-pageArea-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        areaService.addArea(addAreaReqDTO);

        String areaname = "";
        TreeAreaReqDTO treeAreaReqDTO = new TreeAreaReqDTO();
        treeAreaReqDTO.setAreaname(areaname);
        AreaRespDTO areaOutput = areaService.treeArea(treeAreaReqDTO).getData();

        Assert.assertTrue(!ObjectUtils.isEmpty(areaOutput.getAreaname()));

    }

}
