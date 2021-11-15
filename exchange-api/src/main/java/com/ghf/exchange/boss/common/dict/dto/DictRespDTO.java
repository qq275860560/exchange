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
public class DictRespDTO {

    @ApiModelProperty("id，业务上虽然可以冗余id，但不建议直接使用id，1.id主要针对的数据库存储，随机生成，数据迁移时可能会出现麻烦，2:缓存,一般只缓存key一般是有意义的唯一字段，使用id无法享受缓存的好处,或者要浪费一倍缓存空间，并且缓存和数据库同步程序也会非常复杂")
    private long id;

    @ApiModelProperty("字典类型，字典名称，字典英文名称，说明是哪一种业务的字典")
    private String dicttype;

    @ApiModelProperty("字典类型描述，字典中文名称，说明是哪一种业务的字典")
    private String dicttypedesc;

    @ApiModelProperty("字典key，前端下拉，单选，多选的隐藏值,或者后端枚举类的code，或者后端常量名")
    private String dictkey;

    @ApiModelProperty("字典value,前端下拉，单选，多选的显示值,或者后端枚举类的msg，或者后端常量值")
    private String dictvalue;

    @ApiModelProperty("排序,指的是同级顺序，拥有同一字典类型时，兄弟间的顺序,0>1>2>3>4,数字小的优先")
    private int orderNum;

    @ApiModelProperty("状态,0:禁用,1:启用")
    private int status;

}
