package net.risesoft.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.example.entity.User;
import net.risesoft.example.service.UserService;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;

/**
 * 描述：控制层
 */
@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取最新一条数据
     * 
     * @param model
     * @return
     */
    @GetMapping("/index")
    public Y9Result<User> index(Model model) {
        // 插入测试数据
        List<User> list = userService.findAll();
        if (list.size() == 0) {
            User user = new User();
            user.setId(Y9IdGenerator.genId());
            user.setAge(10);
            user.setBirth("2018-11-02");
            user.setCreateTime("2018-11-02");
            user.setEducation("大学");
            user.setFileId("");
            user.setMobile("11111111");
            user.setName("Tony");
            user.setSex("男");
            user = userService.save(user);
        }
        User user2 = userService.findTopByOrderByIdDesc();
        User newUser = userService.findById(user2.getId());
        return Y9Result.success(newUser);
    }

    @PostMapping("/saveUser")
    public Y9Result<User> saveUser(User user) {
        User newUser = userService.saveOrUpdate(user);
        return Y9Result.success(newUser);
    }

    /**
     * 描述：删除
     */
    @PostMapping(value = "/deleteById")
    public Y9Result<Object> deleteById(String id) {
        userService.deleteById(id);
        return Y9Result.success();
    }

}