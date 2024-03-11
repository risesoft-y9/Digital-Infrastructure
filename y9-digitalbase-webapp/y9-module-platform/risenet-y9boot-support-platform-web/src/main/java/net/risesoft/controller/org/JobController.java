package net.risesoft.controller.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Job;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsManager;
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
@IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
public class JobController {

    private final Y9JobService y9JobService;

    /**
     * 根据id列表删除职位
     *
     * @param ids id数组
     * @return
     */
    @RiseLog(operationName = "根据id列表删除职位", operationType = OperationTypeEnum.DELETE)
    @GetMapping("/deleteByIds")
    public Y9Result<Void> deleteById(@RequestParam("ids") @NotEmpty List<String> ids) {
        y9JobService.delete(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据id查找职位
     *
     * @param id 唯一标识
     * @return
     */
    @RiseLog(operationName = "根据id查找职位", operationType = OperationTypeEnum.BROWSE)
    @GetMapping("/getJobById/{id}")
    public Y9Result<Y9Job> getJobById(@PathVariable("id") @NotBlank String id) {
        Y9Job y9Job = y9JobService.getById(id);
        return Y9Result.success(y9Job, "操作成功");
    }

    /**
     * 查找所有职位
     *
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "查找所有职位", operationType = OperationTypeEnum.BROWSE)
    @GetMapping("/listAll")
    public Y9Result<List<Y9Job>> listAll() {
        List<Y9Job> jobList = y9JobService.listAll();
        return Y9Result.success(jobList, "查询成功");
    }

    /**
     * 保存职位排序结果
     *
     * @param jobIds 职位id列表
     * @return
     */
    @RiseLog(operationName = "保存职位排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<List<Y9Job>> saveOrder(@RequestParam(value = "jobIds") List<String> jobIds) {
        List<Y9Job> jobList = y9JobService.order(jobIds);
        return Y9Result.success(jobList, "保存职位排序成功");
    }

    /**
     * 保存或修改职位
     *
     * @param job 职位对象
     * @return
     */
    @RiseLog(operationName = "保存或修改职位", operationType = OperationTypeEnum.MODIFY)
    @PostMapping("/saveOrUpdate")
    public Y9Result<Y9Job> saveOrUpdate(@Validated Y9Job job) {
        Y9Job y9Job = y9JobService.saveOrUpdate(job);
        return Y9Result.success(y9Job, "操作成功");
    }

    /**
     * 根据名称获取职位
     *
     * @param name 职位名称
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据名称获取职位", operationType = OperationTypeEnum.BROWSE)
    @GetMapping("/searchByName")
    public Y9Result<List<Y9Job>> searchByName(@RequestParam String name) {
        List<Y9Job> y9Jobs = y9JobService.listByName(name);
        return Y9Result.success(y9Jobs, "操作成功");
    }
}
