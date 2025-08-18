package net.risesoft.controller.permission.cache;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.permission.vo.ResourcePermissionVO;
import net.risesoft.entity.permission.cache.person.Y9PersonToResourceAndAuthority;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.permission.cache.Y9PersonToResourceAndAuthorityService;

/**
 * 权限展示
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/personResources", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER})
public class PersonResourcesController {

    private final Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;
    private final ResourcePermissionVOBuilder resourcePermissionVOBuilder;

    /**
     * 根据人员id，获取人员的权限缓存列表
     * 
     * @param personId 人员id
     * @return {@code Y9Result<List<ResourcePermissionVO>>}
     */
    @GetMapping("/getByPersonId")
    public Y9Result<List<ResourcePermissionVO>> getByPersonId(@RequestParam @NotBlank String personId) {
        List<Y9PersonToResourceAndAuthority> y9PersonToResourceAndAuthorityList =
            y9PersonToResourceAndAuthorityService.list(personId);
        return Y9Result.success(resourcePermissionVOBuilder
            .buildResourcePermissionVOList(new ArrayList<>(y9PersonToResourceAndAuthorityList)));
    }

}
