package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.org.Job;

/**
 * @author sdb
 * @author ls
 * @date 2022/9/22
 */
public interface Y9JobService {

    Job create(String name, String code);

    /**
     * 根据id数组删除
     *
     * @param ids id数组
     */
    void delete(List<String> ids);

    /**
     * 根据id删除
     *
     * @param id id
     */
    void deleteById(String id);

    /**
     * 根据id获取职位
     *
     * @param id id
     * @return {@code Optional<Job>}
     */
    Optional<Job> findById(String id);

    /**
     * 根据人员id查找其拥有的职位
     *
     * @param personId 人员ID
     * @return {@code List<Job>}
     */
    List<Job> findByPersonId(String personId);

    /**
     * 根据id获取职位
     *
     * @param id id
     * @return {@link Job}
     */
    Job getById(String id);

    /**
     * 获取所有职位
     *
     * @return {@code List<Job>}
     */
    List<Job> listAll();

    /**
     * 根据职位名获取职位
     *
     * @param name 名字
     * @return {@code List<Job>}
     */
    List<Job> listByNameLike(String name);

    /**
     * 按照tabindexs的顺序重新排序职位列表
     *
     * @param jobIds 岗位id
     * @return {@code List<Job>}
     */
    List<Job> order(List<String> jobIds);

    /**
     * 保存或更新职位
     *
     * @param job 职位
     * @return {@link Job}
     */
    Job saveOrUpdate(Job job);
}
