package net.risesoft.controller.org;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9PersonExtService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.relation.Y9PersonsToGroupsService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;
import net.risesoft.y9public.service.user.Y9UserService;

import cn.hutool.core.util.DesensitizedUtil;

/**
 * 人员管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/person", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@IsManager({ManagerLevelEnum.SYSTEM_MANAGER})
public class PersonController {

    private final Y9PersonsToGroupsService y9PersonsToGroupsService;
    private final Y9PersonsToPositionsService y9PersonsToPositionsService;
    private final Y9PersonExtService y9PersonExtService;
    private final Y9PersonService y9PersonService;
    private final Y9UserService y9UserService;
    private final Y9FileStoreService y9FileStoreService;

    /**
     * 为人员添加用户组
     *
     * @param personId 人员id
     * @param groupIds 用户组id数组
     * @return
     */
    @RiseLog(operationName = "为人员添加用户组", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/addGroups")
    public Y9Result<Object> addGroups(@RequestParam @NotBlank String personId,
        @RequestParam @NotEmpty String[] groupIds) {
        y9PersonsToGroupsService.addGroups(personId, groupIds);
        return Y9Result.successMsg("为人员添加用户组成功");
    }

    /**
     * 为人员添加岗位
     *
     * @param personId 人员id
     * @param positionIds 岗位id数组
     * @return
     */
    @RiseLog(operationName = "为人员添加岗位", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/addPositions")
    public Y9Result<List<Y9PersonsToPositions>> addPositions(@RequestParam @NotBlank String personId,
        @RequestParam @NotEmpty String[] positionIds) {
        List<Y9PersonsToPositions> personsToPositionsList =
            y9PersonsToPositionsService.addPositions(personId, positionIds);
        return Y9Result.success(personsToPositionsList, "为人员添加岗位成功");
    }

    /**
     * 根据id，改变人员禁用状态
     *
     * @param id 人员id
     * @return
     */
    @RiseLog(operationName = "禁用人员", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/changeDisabled")
    public Y9Result<Y9Person> changeDisabled(@NotBlank @RequestParam String id) {
        Y9Person y9Person = y9PersonService.changeDisabled(id);
        return Y9Result.success(y9Person, "人员禁用状态修改成功");
    }

    /**
     * 判断CA认证码是否可用（同一个租户）
     *
     * @param caid CA认证码
     * @return
     */
    @RiseLog(operationName = "判断同一个租户CA认证码是否重复")
    @RequestMapping(value = "/checkCaid")
    public Y9Result<Boolean> checkCaid(@RequestParam(required = false) String personId,
        @NotBlank @RequestParam String caid) {
        return Y9Result.success(y9UserService.isCaidAvailable(personId, caid), "判断同一个租户CA认证码是否重复操作成功");
    }

    /**
     * 判断登录名是否可用（同一个租户）
     *
     * @param loginName 登录名称
     * @return
     */
    @RiseLog(operationName = "判断登录名是否可用")
    @RequestMapping(value = "/checkLoginName")
    public Y9Result<Boolean> checkLoginName(@RequestParam(required = false) String personId,
        @NotBlank @RequestParam String loginName) {
        return Y9Result.success(y9PersonService.isLoginNameAvailable(personId, loginName), "判断登录名是否可用成功");
    }

    /**
     * 根据人员id，获取扩展属性
     *
     * @param personId 人员id
     * @return
     */
    @RiseLog(operationName = "获取扩展属性")
    @RequestMapping(value = "/getExtendProperties")
    public Y9Result<String> getExtendProperties(@RequestParam @NotBlank String personId) {
        String properties = y9PersonService.getById(personId).getProperties();
        return Y9Result.success(properties, "获取扩展属性成功");
    }

    /**
     * 根据人员id，获取人员信息
     *
     * @param personId 人员id
     * @return
     */
    @RiseLog(operationName = "根据人员id，获取人员信息")
    @RequestMapping(value = "/getPersonById")
    public Y9Result<Y9Person> getPersonById(@NotBlank @RequestParam String personId) {
        return Y9Result.success(y9PersonService.getById(personId), "根据人员id，获取人员信息成功");
    }

    /**
     * 根据人员id，获取人员扩展信息
     *
     * @param personId 人员id
     * @return
     */
    @RiseLog(operationName = "根据人员id，获取人员扩展信息")
    @RequestMapping(value = "/getPersonExtById")
    public Y9Result<Y9PersonExt> getPersonExtById(@RequestParam @NotBlank String personId) {
        return Y9Result.success(y9PersonExtService.getByPersonId(personId), "根据人员id，获取人员扩展信息成功");
    }

    /**
     * 根据人员id，获取脱敏后的人员扩展信息
     *
     * @param personId 人员id
     * @return
     */
    @RiseLog(operationName = "根据人员id，获取脱敏后的人员扩展信息")
    @RequestMapping(value = "/getPersonExtByIdWithEncry")
    public Y9Result<Y9PersonExt> getPersonExtByIdWithEncry(@RequestParam @NotBlank String personId) {
        Y9PersonExt ext = y9PersonExtService.findByPersonId(personId).orElse(null);
        if (ext != null && StringUtils.isNotBlank(ext.getIdNum()) && ext.getIdNum().length() > 8) {
            ext.setIdNum(DesensitizedUtil.idCardNum(ext.getIdNum(), 6, 3));
        }
        return Y9Result.success(ext, "根据人员id，获取人员敏感信息加密后的扩展信息成功");
    }

    /**
     * 根据用户组id，获取用户组人员列表
     *
     * @param groupId 用户组id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取用户组人员列表")
    @RequestMapping(value = "/listPersonsByGroupId")
    public Y9Result<List<Y9Person>> listPersonsByGroupId(@RequestParam @NotBlank String groupId) {
        return Y9Result.success(y9PersonService.listByGroupId(groupId, null), "获取用户组人员列表成功");
    }

    /**
     * 根据父节点id，获取人员列表
     *
     * @param parentId 父节点id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取人员列表")
    @RequestMapping(value = "/listPersonsByParentId")
    public Y9Result<List<Y9Person>> listPersonsByParentId(@RequestParam @NotBlank String parentId) {
        return Y9Result.success(y9PersonService.listByParentId(parentId, null), "获取人员列表成功");
    }

    /**
     * 根据岗位Id，获取人员列表
     *
     * @param positionId 岗位id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取岗位人员列表")
    @RequestMapping(value = "/listPersonsByPositionId")
    public Y9Result<List<Y9Person>> listPersonsByPositionId(@RequestParam @NotBlank String positionId) {
        return Y9Result.success(y9PersonService.listByPositionId(positionId, null), "获取岗位人员列表成功");
    }

    /**
     * 移动人员到新的节点
     *
     * @param personId 人员id
     * @param parentId 目标父节点id
     * @return
     */
    @RiseLog(operationName = "移动人员到新的节点", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/move")
    public Y9Result<Y9Person> move(@RequestParam @NotBlank String personId, @NotBlank @RequestParam String parentId) {
        Y9Person y9Person = y9PersonService.move(personId, parentId);
        return Y9Result.success(y9Person, "移动人员成功");
    }

    /**
     * 根据id数组，删除人员
     *
     * @param ids 人员id数组
     * @return
     */
    @RiseLog(operationName = "删除人员", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam(value = "ids") @NotEmpty List<String> ids) {
        y9PersonService.delete(ids);
        return Y9Result.successMsg("删除人员成功");
    }

    /**
     * 移除人员关联的用户组
     *
     * @param personId 人员id
     * @param groupIds 用户组id数组
     * @return
     */
    @RiseLog(operationName = "移除人员关联的用户组", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removeGroups")
    public Y9Result<String> removeGroups(@RequestParam @NotBlank String personId,
        @RequestParam("groupIds") @NotEmpty String[] groupIds) {
        y9PersonsToGroupsService.removeGroups(personId, groupIds);
        return Y9Result.successMsg("移除人员关联的用户组成功");
    }

    /**
     * 为人员移除岗位
     *
     * @param personId 人员id
     * @param positionIds 岗位id数组
     * @return
     */
    @RiseLog(operationName = "移除人员的岗位", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removePositions")
    public Y9Result<String> removePositions(@RequestParam @NotBlank String personId,
        @RequestParam("positionIds") @NotEmpty String[] positionIds) {
        y9PersonsToPositionsService.deletePositions(personId, positionIds);
        return Y9Result.successMsg("移除人员的岗位成功");
    }

    /**
     * 重置密码
     *
     * @param personId 人员id
     * @return
     */
    @RiseLog(operationName = "重置密码", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/resetPassword")
    public Y9Result<String> resetPassword(@RequestParam @NotBlank String personId) {
        y9PersonService.resetDefaultPassword(personId);
        return Y9Result.successMsg("重置密码成功");
    }

    /**
     * 上传个人头像
     * 
     * @param iconFile 头像
     * @return
     */
    @PostMapping(value = "/saveAvator")
    public Y9Result<String> saveAvator(@RequestParam(required = false) MultipartFile iconFile,
        @RequestParam @NotBlank String personId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            if (iconFile != null) {
                String fileType = FilenameUtils.getExtension(iconFile.getOriginalFilename());
                String fileNewName = personId + "_" + sdf.format(new Date()) + "." + fileType;
                String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), "avator");
                Y9FileStore y9FileStore = y9FileStoreService.uploadFile(iconFile, fullPath, fileNewName);
                String url = y9FileStore.getUrl();
                y9PersonService.saveAvator(personId, url);
                return Y9Result.success(url, "保存个人头像成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存个人头像失败！");
        }
        return Y9Result.failure("保存个人头像失败！未上传图片！");
    }

    /**
     * 保存扩展属性(直接覆盖)
     *
     * @param personId 人员id
     * @param properties 属性值
     * @return
     */
    @RiseLog(operationName = "保存扩展属性", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveExtendProperties")
    public Y9Result<String> saveExtendProperties(@RequestParam @NotBlank String personId,
        @RequestParam String properties) {
        Y9Person y9Person = y9PersonService.saveProperties(personId, properties);
        return Y9Result.success(y9Person.getProperties(), "保存扩展属性成成功");
    }

    /**
     * 新建或者更新人员信息
     *
     * @param person 人员实体
     * @param ext 人员扩展信息实体
     * @param jobIds 职位id数组
     * @param positionIds 岗位id数组
     * @return
     */
    @RiseLog(operationName = "新建或者更新人员信息", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Y9Person> saveOrUpdate(@Validated Y9Person person, @Validated Y9PersonExt ext,
        @RequestParam(value = "positionIds", required = false) List<String> positionIds,
        @RequestParam(value = "jobIds", required = false) List<String> jobIds) {
        Y9Person y9Person = y9PersonService.saveOrUpdate(person, ext, positionIds, jobIds);
        return Y9Result.success(y9Person, "保存人员信息成功");
    }

    /**
     * 保存排序结果
     *
     * @param personIds 人员id数组
     * @return
     */
    @RiseLog(operationName = "保存人员排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam(value = "personIds") @NotEmpty List<String> personIds) {
        y9PersonService.order(personIds);
        return Y9Result.successMsg("保存人员排序成功");
    }

    /**
     * 上传个人证件照图片
     *
     * @param iconFile 头像文件
     * @param personId 人员id
     * @return
     */
    @RiseLog(operationType = OperationTypeEnum.ADD, operationName = "上传个人证件照图片")
    @PostMapping(value = "/savePersonPhoto")
    public Y9Result<String> savePersonPhoto(@RequestParam MultipartFile iconFile,
        @RequestParam @NotBlank String personId) {
        if (iconFile != null && iconFile.getSize() != 0) {
            byte[] photo = null;
            try {
                photo = iconFile.getBytes();
            } catch (IOException e1) {
                LOGGER.warn(e1.getMessage(), e1);
            }
            Y9Person person = y9PersonService.getById(personId);
            y9PersonExtService.savePersonPhoto(person, photo);

            return Y9Result.successMsg("上传个人证件照图片成功");
        } else {
            return Y9Result.failure("上传个人证件照图片失败，图片为空");
        }
    }

    /**
     * 上传个人签名图片
     *
     * @param iconFile 签名文件
     * @param personId 人员id
     * @return
     */
    @RiseLog(operationType = OperationTypeEnum.ADD, operationName = "上传个人签名照图片")
    @PostMapping(value = "/savePersonSign")
    public Y9Result<String> savePersonSign(@RequestParam MultipartFile iconFile,
        @RequestParam @NotBlank String personId) {
        if (iconFile != null && iconFile.getSize() != 0) {
            byte[] photo = null;
            try {
                photo = iconFile.getBytes();
            } catch (IOException e1) {
                LOGGER.warn(e1.getMessage(), e1);
            }
            Y9Person person = y9PersonService.getById(personId);
            y9PersonExtService.savePersonSign(person, photo);

            return Y9Result.successMsg("上传个人签名图片成功");
        } else {
            return Y9Result.failure("上传个人签名图片失败，图片为空");
        }
    }

    /**
     * 保存添加人员
     *
     * @param personIds 人员Id数组
     * @param parentId 父节点id
     * @return
     */
    @RiseLog(operationName = "保存添加人员", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/savePersons")
    public Y9Result<List<Y9Person>> savePersons(@RequestParam(value = "personIds") @NotEmpty List<String> personIds,
        @RequestParam String parentId) {
        List<Y9Person> persons = y9PersonService.addPersons(parentId, personIds);
        return Y9Result.success(persons, "保存添加人员成功");
    }

    /**
     * 查看人员证件照片
     *
     * @param personId 人员id
     * @param response
     *
     */
    @RiseLog(operationName = "查看人员证件照片")
    @RequestMapping("/showPersonPhoto")
    public void showPersonPhoto(@RequestParam @NotBlank String personId, HttpServletResponse response) {
        Y9Person person = y9PersonService.getById(personId);
        try (ServletOutputStream sos = response.getOutputStream()) {
            response.setContentType("image/jpg");
            Y9PersonExt ext = y9PersonExtService.findByPersonId(person.getId()).orElse(null);
            if (ext != null && ext.getPhoto() != null) {
                InputStream inputStream = new ByteArrayInputStream(ext.getPhoto());
                byte[] b = new byte[1024];
                int l;
                while ((l = inputStream.read(b)) > -1) {
                    sos.write(b, 0, l);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 查看人员签名照片
     *
     * @param personId 人员id
     * @param response
     *
     */
    @RiseLog(operationName = "查看人员签名照片")
    @RequestMapping("/showPersonSign")
    public void showPersonSign(@RequestParam @NotBlank String personId, HttpServletResponse response) {
        Y9Person person = y9PersonService.getById(personId);
        try (ServletOutputStream out = response.getOutputStream()) {
            Y9PersonExt ext = y9PersonExtService.findByPersonId(person.getId()).orElse(null);
            if (ext != null && ext.getSign() != null) {
                InputStream inputStream = new ByteArrayInputStream(ext.getSign());
                byte[] b = new byte[1024];
                int l;
                while ((l = inputStream.read(b)) > -1) {
                    out.write(b, 0, l);
                }
                ImageIO.createImageOutputStream(out);
                out.flush();
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
