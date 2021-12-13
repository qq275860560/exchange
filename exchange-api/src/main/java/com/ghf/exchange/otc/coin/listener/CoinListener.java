package com.ghf.exchange.otc.coin.listener;

import com.ghf.exchange.boss.authorication.client.service.ClientService;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class CoinListener {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private ClientService clientService;

}