package com.ghf.exchange.otc.legalcurrency.entity;

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
@Table(name = "t_legal_currency")
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
@Data
@Slf4j
public class LegalCurrency {

    @ApiModelProperty("id")
    @Id
    @Column(name = "id")
    private long id;

    @ApiModelProperty("法币编码")
    @Column(name = "legal_currency_code")
    private String legalCurrencyCode;

    @ApiModelProperty("法币名称")
    @Column(name = "legal_currency_name")
    private String legalCurrencyName;

    @ApiModelProperty("法币符号")
    @Column(name = "legal_currency_symbol")
    private String legalCurrencySymbol;

    @ApiModelProperty("法币单位")
    @Column(name = "legal_currency_unit")
    private String legalCurrencyUnit;

    @ApiModelProperty("法币所在国家编码")
    @Column(name = "legal_currency_country_code")
    private String legalCurrencyCountryCode;

    @ApiModelProperty("法币所在国家名称")
    @Column(name = "legal_currency_country_name")
    private String legalCurrencyCountryName;

    @ApiModelProperty("汇率，兑美元汇率")
    @Column(name = "legal_currency_exchange_rate")
    private String legalCurrencyExchangeRate;

    @ApiModelProperty("状态,0:禁用,1:启用")
    @Column(name = "status")
    private int status;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

}
