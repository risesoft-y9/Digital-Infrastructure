package net.risesoft.controller.identity;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.identity.vo.ResourcePermissionVO;
import net.risesoft.entity.identity.person.Y9PersonToResourceAndAuthority;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.identity.Y9PersonToResourceAndAuthorityService;

/**
 * 权限展示
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/personResources", produces = "application/json")
@RequiredArgsConstructor
@Validated
public class PersonResourcesController {

    private final Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;
    private final ResourcePermissionVOBuilder resourcePermissionVOBuilder;

    @GetMapping
    public Y9Result<List<ResourcePermissionVO>> getByPersonId(@RequestParam @NotBlank String personId) {
        List<Y9PersonToResourceAndAuthority> y9PersonToResourceAndAuthorityList =
            y9PersonToResourceAndAuthorityService.list(personId);
        return Y9Result.success(resourcePermissionVOBuilder
            .buildResourcePermissionVOList(new ArrayList<>(y9PersonToResourceAndAuthorityList)));
    }

}
