package net.risesoft.controller.identity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.identity.vo.ResourcePermissionVO;
import net.risesoft.entity.identity.position.Y9PositionToResourceAndAuthority;
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
@RequestMapping(value = "/api/rest/positionResources", produces = "application/json")
@RequiredArgsConstructor
public class PositionResourcesController {

    private final Y9PositionToResourceAndAuthorityService y9PositionToResourceAndAuthorityService;
    private final ResourcePermissionVOBuilder resourcePermissionVOBuilder;

    @GetMapping
    public Y9Result<List<ResourcePermissionVO>> getByPositionId(String positionId) {
        List<Y9PositionToResourceAndAuthority> y9PositionToResourceAndAuthorityList =
            y9PositionToResourceAndAuthorityService.list(positionId);
        return Y9Result.success(resourcePermissionVOBuilder
            .buildResourcePermissionVOList(new ArrayList<>(y9PositionToResourceAndAuthorityList)));
    }

}
