package net.risesoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.elastic.entity.User;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.UserService;

/**
 * 描述：控制层
 */
@Controller
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
    @RequestMapping("/index")
    public String index(Model model) {
        // 插入测试数据
        Iterable<User> list = userService.findAll();
        if (!list.iterator().hasNext()) {
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
            userService.save(user);

            User user2 = new User();
            user2.setId(Y9IdGenerator.genId());
            user2.setAge(18);
            user2.setBirth("2018-11-02");
            user2.setCreateTime("2018-11-02");
            user2.setEducation("大学");
            user2.setFileId("");
            user2.setMobile("11111111");
            user2.setName("sun");
            user2.setSex("男");
            userService.save(user2);
        }
        return "index.html";
    }

    @PostMapping("/saveUser")
    @ResponseBody
    public Y9Result<User> saveUser(User user) {
        User newUser = userService.saveOrUpdate(user);
        return Y9Result.success(newUser);
    }

    /**
     * 描述：删除
     */
    @PostMapping(value = "/deleteById")
    @ResponseBody
    public Y9Result<Object> deleteById(String id) {
        userService.deleteById(id);
        return Y9Result.success();
    }

    @GetMapping("/page")
    @ResponseBody
    public Page<User> page(String name, String mobile, Integer page, Integer limit, Model model) {
        // 插入测试数据
        Iterable<User> list = userService.findAll();
        if (!list.iterator().hasNext()) {
            User user = new User();
            user.setId(Y9IdGenerator.genId());
            user.setAge(18);
            user.setBirth("2018-11-08");
            user.setCreateTime("2018-11-08");
            user.setEducation("大学");
            user.setFileId("");
            user.setMobile("11111111");
            user.setName("sun");
            user.setSex("男");
            user = userService.save(user);
        }
        return userService.search(name, mobile, page, limit);
    }
}