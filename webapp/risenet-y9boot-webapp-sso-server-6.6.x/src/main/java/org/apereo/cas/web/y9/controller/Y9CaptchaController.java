package org.apereo.cas.web.y9.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apereo.cas.web.y9.util.common.Base64Util;
import org.apereo.cas.web.y9.util.common.ValidatorCodeUtil;
import org.apereo.cas.web.y9.util.common.ValidatorCodeUtil.ValidatorCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/api")
@Slf4j
public class Y9CaptchaController {

    @ResponseBody
    @RequestMapping(value = "/getCaptchaData")
    public Map<String, Object> getCaptchaData() {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);

        ValidatorCode vcode = ValidatorCodeUtil.getCode();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(vcode.getImage(), "jpg", baos);
            byte[] bs = baos.toByteArray();
            String data = Base64Util.encode(bs);
            data = "data:image/jpg;base64," + data;
            map.put("data", data);
            map.put("code", vcode.getCode());
            baos.flush();
        } catch (IOException e) {
            map.put("success", false);
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

}
