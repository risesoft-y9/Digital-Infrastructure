package net.risesoft.api.platform.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.platform.org.dto.CreateJobDTO;
import net.risesoft.model.platform.Job;
import net.risesoft.pojo.Y9Result;

/**
 * 职位服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2025/3/19
 * @since 9.6.8
 */
@Validated
public interface JobApi {

    /**
     * 新建职位
     *
     * @param tenantId 租户id
     * @param job 职位对象
     * @return {@code Y9Result<Job>} 通用请求返回对象 - data 是保存的职位
     * @since 9.6.0
     */
    @PostMapping("/create")
    Y9Result<Job> create(@RequestParam("tenantId") @NotBlank String tenantId, @Validated @RequestBody CreateJobDTO job);

    /**
     * 获取所有职位
     * 
     * @return {@code Y9Result<List<Job>>} 通用请求返回对象 - data 是职位列表
     */
    @GetMapping("/listAll")
    Y9Result<List<Job>> listAll(@RequestParam("tenantId") @NotBlank String tenantId);
}
