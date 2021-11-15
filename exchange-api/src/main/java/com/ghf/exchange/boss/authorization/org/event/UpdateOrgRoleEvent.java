package com.ghf.exchange.boss.authorization.org.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.Set;

/**
 * @author jiangyuanlin@163.com
 */

@Slf4j
public class UpdateOrgRoleEvent extends ApplicationEvent {

    @Data
    @Builder
    public static class Payload {
        @ApiModelProperty("组织名称,组织英文名称，组织编码，唯一")
        private String orgname;

        @ApiModelProperty("角色名称列表，角色英文名称列表，角色编码列表")
        private Set<String> rolenameSet = Collections.emptySet();
    }

    public UpdateOrgRoleEvent(Payload source) {
        super(source);
    }
}
