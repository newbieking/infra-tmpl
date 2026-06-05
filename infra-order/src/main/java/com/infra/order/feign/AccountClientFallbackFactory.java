package com.infra.order.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccountClientFallbackFactory implements FallbackFactory<AccountClient> {

    private static final Logger log = LoggerFactory.getLogger(AccountClientFallbackFactory.class);

    @Override
    public AccountClient create(Throwable cause) {
        log.error("[Feign Fallback] infra-account 服务调用失败: {}", cause.getMessage());
        return (userId, amount) -> {
            throw new RuntimeException("账户服务不可用，请稍后重试", cause);
        };
    }
}
