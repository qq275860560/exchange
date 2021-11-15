package com.ghf.exchange.boss.common.dict.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Proxy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jiangyuanlin@163.com
 */
@ApiModel
@Table(name = "t_dict")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class Dict {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("字典类型，字典名称，字典英文名称，说明是哪一种业务的字典")
    @Column(name = "dict_type")
    private String dicttype;

    @ApiModelProperty("字典类型描述，字典中文名称，说明是哪一种业务的字典")
    @Column(name = "dict_type_desc")
    private String dicttypedesc;

    @ApiModelProperty("字典key，前端下拉，单选，多选的隐藏值,或者后端枚举类的code，或者后端常量名")
    @Column(name = "dict_key")
    private String dictkey;

    @ApiModelProperty("字典value,前端下拉，单选，多选的显示值,或者后端枚举类的msg，或者后端常量值")
    @Column(name = "dict_value")
    private String dictvalue;

    @ApiModelProperty("排序,指的是同级顺序，拥有同一字典类型时，兄弟间的顺序,0>1>2>3>4,数字小的优先")
    @Column(name = "order_num")
    private int orderNum;

    @ApiModelProperty("状态,0:禁用,1:启用")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("创建人id")
    @Column(name = "create_user_id")
    private long createUserId;

    @ApiModelProperty("创建人名称")
    @Column(name = "create_user_name")
    private String createUserName;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

}
