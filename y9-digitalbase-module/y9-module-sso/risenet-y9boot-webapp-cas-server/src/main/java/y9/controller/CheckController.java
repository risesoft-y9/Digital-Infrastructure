package y9.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import y9.util.Y9Context;
import y9.util.Y9Result;

@Controller
@RequestMapping(value = "/api")
@Slf4j
@RequiredArgsConstructor
public class CheckController {

    @ResponseBody
    @GetMapping(value = "/getRandom")
    public Y9Result<Object> getRandom() {
        try {
            return Y9Result.success(Y9Context.getProperty("y9.login.encryptionRsaPublicKey"), "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("获取失败");
        }
    }

}
