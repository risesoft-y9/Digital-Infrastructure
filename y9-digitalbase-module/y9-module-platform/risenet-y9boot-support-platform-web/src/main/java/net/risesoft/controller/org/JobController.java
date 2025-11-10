package net.risesoft.controller.org;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.org.Job;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9JobService;

/**
 * 职位管理
 *
 * @author shidaobang
 * @author lianshen
 * @date 2022/9/22
 * @since 9.6.0
 */
@Validated
@RestController
@RequestMapping("/api/rest/job")
@RequiredArgsConstructor
@IsAnyManager(ManagerLevelEnum.SYSTEM_MANAGER)
public class JobController {

    private final Y9JobService y9JobService;

    /**
     * 根据id列表删除职位
     *
     * @param ids id数组
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "根据id列表删除职位", operationType = OperationTypeEnum.DELETE)
    @GetMapping("/deleteByIds")
    public Y9Result<Object> deleteById(@RequestParam("ids") @NotEmpty List<String> ids) {
        y9JobService.delete(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据id查找职位
     *
     * @param id 唯一标识
     * @return {@code Y9Result<Y9Job>}
     */
    @RiseLog(operationName = "根据id查找职位", operationType = OperationTypeEnum.BROWSE)
    @GetMapping("/getJobById/{id}")
    public Y9Result<Job> getJobById(@PathVariable("id") @NotBlank String id) {
        return Y9Result.success(y9JobService.getById(id), "操作成功");
    }

    /**
     * 查找所有职位
     *
     * @return {@code Y9Result<List<Y9Job>>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "查找所有职位", operationType = OperationTypeEnum.BROWSE)
    @GetMapping("/listAll")
    public Y9Result<List<Job>> listAll() {
        return Y9Result.success(y9JobService.listAll(), "查询成功");
    }

    /**
     * 保存或修改职位
     *
     * @param job 职位对象
     * @return {@code Y9Result<Y9Job>}
     */
    @RiseLog(operationName = "保存或修改职位", operationType = OperationTypeEnum.MODIFY)
    @PostMapping("/saveOrUpdate")
    public Y9Result<Job> saveOrUpdate(@Validated Job job) {
        return Y9Result.success(y9JobService.saveOrUpdate(job), "操作成功");
    }

    /**
     * 保存职位排序结果
     *
     * @param jobIds 职位id列表
     * @return {@code Y9Result<List<Y9Job>>}
     */
    @RiseLog(operationName = "保存职位排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<List<Job>> saveOrder(@RequestParam(value = "jobIds") List<String> jobIds) {
        List<Job> jobList = y9JobService.order(jobIds);
        return Y9Result.success(jobList, "保存职位排序成功");
    }

    /**
     * 根据名称获取职位
     *
     * @param name 职位名称
     * @return {@code Y9Result<List<Y9Job>>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据名称获取职位", operationType = OperationTypeEnum.BROWSE)
    @GetMapping("/searchByName")
    public Y9Result<List<Job>> searchByName(@RequestParam String name) {
        List<Job> jobList = y9JobService.listByNameLike(name);
        return Y9Result.success(jobList, "操作成功");
    }
}
