package com.infra.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "infra-account", fallbackFactory = AccountClientFallbackFactory.class)
public interface AccountClient {

    @PostMapping("/api/v1/account/deduct")
    Map<String, Object> deductBalance(@RequestParam String userId, @RequestParam int amount);
}
