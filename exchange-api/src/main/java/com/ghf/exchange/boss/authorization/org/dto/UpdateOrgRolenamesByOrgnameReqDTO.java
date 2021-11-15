package com.ghf.exchange.boss.authorization.org.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class UpdateOrgRolenamesByOrgnameReqDTO {

    @ApiModelProperty("组织名称,组织英文名称，组织编码，唯一")
    private String orgname;

    @ApiModelProperty("角色名称列表，冗余,逗号隔开，前后都有逗号")
    private String rolenames;

    @ApiModelProperty("角色描述列表，冗余,逗号隔开，前后都有逗号")
    private String roledescs;

}
