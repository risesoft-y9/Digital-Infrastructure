package org.apereo.cas.web.y9.controller;

import org.apereo.cas.web.y9.util.Y9Context;
import org.apereo.cas.web.y9.util.Y9Result;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Lazy(false)
@Controller
@RequestMapping(value = "/api")
@Slf4j
public class RandomController {

    @ResponseBody
    @GetMapping(value = "/getRandom")
    public Y9Result<Object> getRandom() {
        try {
            return Y9Result.success(Y9Context.getProperty("y9.encryptionRsaPublicKey"), "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("获取失败");
        }
    }

}
