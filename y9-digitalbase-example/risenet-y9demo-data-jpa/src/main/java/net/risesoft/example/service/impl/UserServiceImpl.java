package net.risesoft.example.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.example.entity.User;
import net.risesoft.example.repository.UserRepository;
import net.risesoft.example.service.UserService;
import net.risesoft.id.Y9IdGenerator;

/**
 * 描述：人员 服务实现层
 */
@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void deleteById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findTopByOrderByIdDesc() {
        return userRepository.findTopByOrderByIdDesc();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User saveOrUpdate(User user) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        User u = new User();
        if (StringUtils.isBlank(user.getId())) {
            u.setId(Y9IdGenerator.genId());
            u.setCreateTime(sdf.format(new Date()));
        } else {
            u = userRepository.findById(user.getId()).orElse(null);
        }
        u.setName(user.getName());
        u.setAge(user.getAge());
        u.setBirth(user.getBirth());
        u.setEducation(user.getEducation());
        u.setMobile(user.getMobile());
        u.setSex(user.getSex());
        return userRepository.save(u);

    }

}