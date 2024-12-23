package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.elastic.entity.User;
import net.risesoft.elastic.repository.UserRepository;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.service.UserService;

/**
 * 描述：人员 服务实现层
 */
@RequiredArgsConstructor
@Service(value = "userService")
public class UserServiceImpl implements UserService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final UserRepository userRepository;

    @Override
    public void deleteById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public Iterable<User> findAll() {
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

    @Override
    public Page<User> search(String name, String mobile, Integer page, Integer rows) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, sort);
        Criteria criteria = new Criteria("name").exists();
        if (StringUtils.isNotBlank(name)) {
            criteria.subCriteria(new Criteria("name").contains(name));
        }
        if (StringUtils.isNotBlank(mobile)) {
            criteria.subCriteria(new Criteria("mobile").contains(mobile));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        SearchHits<User> search = elasticsearchOperations.search(query, User.class);

        List<User> list = search.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<User> pageResult = new PageImpl<>(list, pageable, search.getTotalHits());
        return pageResult;
    }

}