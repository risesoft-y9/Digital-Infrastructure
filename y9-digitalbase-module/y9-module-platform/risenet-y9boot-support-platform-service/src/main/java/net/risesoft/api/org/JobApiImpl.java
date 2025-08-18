package net.risesoft.api.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.JobApi;
import net.risesoft.api.platform.org.dto.CreateJobDTO;
import net.risesoft.entity.org.Y9Job;
import net.risesoft.model.platform.org.Job;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;

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
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/job", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class JobApiImpl implements JobApi {

    private final Y9JobService y9JobService;

    /**
     * 创建职位
     *
     * @param tenantId 租户id
     * @param job 职位对象
     * @return {@code Y9Result<Position>} 通用请求返回对象 - data 是保存的岗位对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Job> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody @Validated CreateJobDTO job) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Job y9Job = Y9ModelConvertUtil.convert(job, Y9Job.class);
        y9Job = y9JobService.saveOrUpdate(y9Job);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Job, Job.class));
    }

    @Override
    public Y9Result<List<Job>> listAll(@RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9Job> jobList = y9JobService.listAll();
        return Y9Result.success(Y9ModelConvertUtil.convert(jobList, Job.class));
    }
}
