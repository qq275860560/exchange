package com.ghf.exchange.boss.common.dict.dto;

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
public class GetDictByDicttypeAndDictkeyReqDTO {

    @ApiModelProperty("字典类型，字典名称，字典英文名称，说明是哪一种业务的字典")
    private String dicttype;

    @ApiModelProperty("字典key，前端下拉，单选，多选的隐藏值,或者后端枚举类的code，或者后端常量名")
    private String dictkey;
}
