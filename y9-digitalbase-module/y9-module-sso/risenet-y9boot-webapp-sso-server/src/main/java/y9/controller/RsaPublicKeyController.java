package y9.controller;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import y9.util.Y9Context;
import y9.util.Y9Result;

@Lazy(false)
@Controller
@RequestMapping(value = "/api")
public class RsaPublicKeyController {

    @ResponseBody
    @GetMapping(value = "/getRsaPublicKey")
    public Y9Result<Object> getRsaPublicKey() {
        try {
            return Y9Result.success(Y9Context.getProperty("y9.rsaPublicKey"), "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("获取失败");
        }
    }

}
