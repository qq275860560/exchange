package com.ghf.exchange.boss.authorization.org.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ghf.exchange.dto.PageReqDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiangyuanlin@163.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class PageOrgReqDTO extends PageReqDTO {

    @ApiModelProperty("组织名称,组织英文名称，组织编码，唯一")
    private String orgname = "";

    @ApiModelProperty("组织描述,组织中文名称，页面显示名称")
    private String orgdesc;

    @ApiModelProperty("组织类型,组织级别，默认为0，其中1:一级组织，2：二级组织,3:三级组织，4:四级组织，5:五级组织")
    private int orgType = 0;

    @ApiModelProperty("角色名称，角色英文名称，角色编码，必须在角色表存在")
    private String rolename = "";

    @ApiModelProperty("角色描述，角色中文名称,比如平台管理员,普通注册用户")
    private String roledesc;

}
