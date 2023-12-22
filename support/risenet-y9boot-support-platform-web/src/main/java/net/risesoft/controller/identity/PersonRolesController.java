package net.risesoft.controller.identity;

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

import net.risesoft.controller.identity.vo.RolePermissionVO;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.identity.Y9PersonToRoleService;

/**
 * 权限展示
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/personRoles", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
public class PersonRolesController {

    private final RolePermissionVOBuilder rolePermissionVOBuilder;
    private final Y9PersonToRoleService y9PersonToRoleService;

    @GetMapping
    public Y9Result<List<RolePermissionVO>> getByPersonId(@RequestParam @NotBlank String personId) {
        List<Y9PersonToRole> rolePermissionVOList = y9PersonToRoleService.listByPersonId(personId);
        return Y9Result
            .success(rolePermissionVOBuilder.buildRolePermissionVOList(new ArrayList<>(rolePermissionVOList)));
    }

}
