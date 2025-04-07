package net.risesoft.demo.sso.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.model.user.UserInfo;

import jakarta.servlet.http.HttpSession;
import net.risesoft.demo.sso.resource.Bar;

@RestController
@RequestMapping(value = "/bars")
public class BarController {

    private static final Logger logger = LoggerFactory.getLogger(BarController.class);

    @GetMapping(value = "/{id}")
    public Bar findOne(@PathVariable Long id) {
        return new Bar(nextLong(), nextString());
    }

    @GetMapping
    public List<Bar> findAll(HttpSession session) {
    	String loginName = (String)session.getAttribute("loginName");
    	UserInfo userInfo = (UserInfo)session.getAttribute("userInfo");
    	
        List<Bar> barList = new ArrayList<Bar>();
        barList.add(new Bar(nextLong(), "用户: " + loginName));
        barList.add(new Bar(nextLong(), "租户: " + userInfo.getTenantName()));
        barList.add(new Bar(nextLong(), "岗位: " + userInfo.getPositionId()));
        barList.add(new Bar(nextLong(), "随机: " + nextString()));
        return barList;
    }
    
    private Long nextLong() {
        return Long.parseLong(RandomStringUtils.secure().nextNumeric(4));
    }
    
    private String nextString() {
        return RandomStringUtils.secure().nextAlphabetic(4);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void create(@RequestBody Bar newBar) {
        logger.info("Bar created");
    }

}