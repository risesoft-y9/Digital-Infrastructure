package y9.controller;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;

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
