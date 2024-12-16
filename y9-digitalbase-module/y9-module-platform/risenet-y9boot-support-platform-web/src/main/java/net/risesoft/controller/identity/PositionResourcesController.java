package net.risesoft.controller.identity;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.identity.vo.ResourcePermissionVO;
import net.risesoft.entity.identity.position.Y9PositionToResourceAndAuthority;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.identity.Y9PositionToResourceAndAuthorityService;

/**
 * 权限展示
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/positionResources", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER})
public class PositionResourcesController {

    private final Y9PositionToResourceAndAuthorityService y9PositionToResourceAndAuthorityService;
    private final ResourcePermissionVOBuilder resourcePermissionVOBuilder;

    /**
     * 根据岗位id，获取岗位的权限缓存列表
     *
     * @param positionId 人员id
     * @return {@code Y9Result<List<ResourcePermissionVO>>}
     */
    @GetMapping("/getByPositionId")
    public Y9Result<List<ResourcePermissionVO>> getByPositionId(@RequestParam @NotBlank String positionId) {
        List<Y9PositionToResourceAndAuthority> y9PositionToResourceAndAuthorityList =
            y9PositionToResourceAndAuthorityService.list(positionId);
        return Y9Result.success(resourcePermissionVOBuilder
            .buildResourcePermissionVOList(new ArrayList<>(y9PositionToResourceAndAuthorityList)));
    }
}
