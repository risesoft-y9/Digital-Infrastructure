package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
public class TestController {

    @GetMapping("/userInfo")
    public Y9Result<UserInfo> getUserInfo() {
        return Y9Result.success(Y9LoginUserHolder.getUserInfo());
    }

}
