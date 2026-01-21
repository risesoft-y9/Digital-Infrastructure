package net.risesoft.controller.resource;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

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

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.AppIcon;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.vo.resource.AppIconDTO;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9public.service.Y9FileStoreService;
import net.risesoft.y9public.service.resource.Y9AppIconService;

/**
 * 图标管理
 *
 * @author mengjuhua
 * @date 2022/06/21
 */
@RestController
@RequestMapping(value = "/api/rest/appIcon", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@Validated
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
public class AppIconController {

    private final Y9AppIconService appIconService;
    private final Y9FileStoreService y9FileStoreService;

    /**
     * 删除图标
     *
     * @param id 应用图标id
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除图标")
    @PostMapping(value = "/deleteIcon")
    public Y9Result<String> deleteIcon(@RequestParam @NotBlank String id) {
        appIconService.delete(id);
        return Y9Result.successMsg("删除成功！");
    }

    /**
     * 获取应用图标
     *
     * @param id 应用图标id
     * @return {@code Y9Result<AppIcon>}
     */
    @RiseLog(operationName = "获取应用图标")
    @RequestMapping("/getAppIconById")
    public Y9Result<AppIcon> getAppIconById(@RequestParam @NotBlank String id) {
        return Y9Result.success(appIconService.getById(id), "获取成功！");
    }

    /**
     * 图片文件读取
     *
     * @return {@code Y9Result<List<AppIcon>>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "图片列表的读取")
    @RequestMapping(value = "/listAll")
    public Y9Result<List<AppIcon>> listAll() {
        return Y9Result.success(appIconService.listAll(), "图片列表的读取！");
    }

    /**
     * 根据名称，获取图片列表
     *
     * @param name 图标名称
     *
     * @return {@code Y9Result<List<AppIcon>>}
     * @since 9.6.8
     */
    @RiseLog(operationName = "根据名称，获取图片列表")
    @RequestMapping(value = "/listByName")
    public Y9Result<List<AppIcon>> listByName(@RequestParam @NotBlank String name) {
        return Y9Result.success(appIconService.listByName(name), "图片列表的读取！");
    }

    /**
     * 查询图标分页列表
     *
     * @param pageQuery 分页信息
     * @return {@code Y9Page<AppIcon>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "查看图标")
    @RequestMapping("/pageAppIcons")
    public Y9Page<AppIcon> pageAppIcons(Y9PageQuery pageQuery) {
        return appIconService.pageAll(pageQuery);
    }

    /**
     * 刷新图标数据
     *
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationType = OperationTypeEnum.MODIFY, operationName = "刷新图标数据")
    @RequestMapping(value = "/refreshAppIconDatas")
    public Y9Result<String> refreshAppIconDatas() {
        appIconService.refreshAppIconData();
        return Y9Result.successMsg("刷新成功！");
    }

    /**
     * 保存图标修改信息
     *
     * @param name 图标名称
     * @param remark 描述
     * @param id 图标id
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationType = OperationTypeEnum.MODIFY, operationName = "修改图标")
    @PostMapping(value = "/saveIcon")
    public Y9Result<String> saveIcon(@RequestParam @NotBlank String name, @RequestParam String remark,
        @RequestParam @NotBlank String id) {
        AppIcon appIcon = appIconService.getById(id);
        appIcon.setRemark(remark);
        appIcon.setName(name);
        if (appIcon.getIconData() == null) {
            try {
                byte[] b = y9FileStoreService.downloadFileToBytes(appIcon.getPath());
                String iconData = Base64.getEncoder().encodeToString(b);
                appIcon.setIconData(iconData);
            } catch (Exception e1) {
                LOGGER.warn(e1.getMessage(), e1);
                return Y9Result.failure("保存失败；图标路径不存在,找不到指定的文件!");
            }
        }
        appIconService.save(appIcon);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 根据名称搜索图标
     *
     * @param name 图标名
     * @return {@code Y9Result<List<AppIcon>>}
     */
    @RiseLog(operationName = "根据名称搜索图标")
    @RequestMapping(value = "/searchAppIcon")
    public Y9Result<List<AppIcon>> searchAppIcon(@RequestParam String name) {
        return Y9Result.success(appIconService.listByName(name), "图标列表搜索成功");
    }

    /**
     * 按名字模糊图标
     *
     * @param pageQuery 分页信息
     * @param name 图标名称
     * @return {@code Y9Page<AppIcon>}
     */
    @RiseLog(operationName = "搜索图标")
    @RequestMapping("/searchIconPageByName")
    public Y9Page<AppIcon> searchIconPageByName(Y9PageQuery pageQuery, String name) {
        return appIconService.pageByName(name, pageQuery);
    }

    /**
     * 上传图标
     *
     * @param iconFile 需要上传的图标文件
     * @param remark 备注
     * @return {@code Y9Result<String>}
     * @throws Y9BusinessException 业务异常
     */
    @RiseLog(operationType = OperationTypeEnum.ADD, operationName = "上传图标")
    @RequestMapping("/uploadIcon")
    public Y9Result<String> uploadIcon(@RequestParam MultipartFile iconFile, @RequestParam String remark)
        throws Y9BusinessException {
        if (iconFile != null && !iconFile.isEmpty()) {
            // 文件名称
            String originalFilename = iconFile.getOriginalFilename();
            // 图片名称
            String imgName = FilenameUtils.getName(originalFilename);
            // 文件类型
            List<AppIcon> appIconList = appIconService.listByName(imgName);
            appIconService.save(iconFile, remark);
            if (!appIconList.isEmpty()) {
                return Y9Result.success("上传成功,文件重名，图标已被覆盖!");
            } else {
                return Y9Result.success("上传成功");
            }
        } else {
            return Y9Result.failure("上传失败！请选择图标文件！");
        }
    }

    /**
     * 上传图标
     *
     * @param appIconDTO 需要上传的图标文件
     * @return {@code Y9Result<String>}
     * @throws Y9BusinessException 业务异常
     * @since 9.6.8
     */
    @RiseLog(operationType = OperationTypeEnum.ADD, operationName = "上传图标")
    @RequestMapping("/uploadIcon4Colors")
    public Y9Result<String> uploadIcon4Colors(AppIconDTO appIconDTO) {
        if (appIconDTO != null && appIconDTO.getIconFiles().length > 0) {
            MultipartFile[] iconFiles = appIconDTO.getIconFiles();
            String[] colors = appIconDTO.getColors();
            StringBuilder msgBuilder = new StringBuilder();
            for (int i = 0, len = iconFiles.length; i < len; i++) {
                MultipartFile file = iconFiles[i];
                if (!file.isEmpty()) {
                    // 文件名称
                    String originalFilename = file.getOriginalFilename();
                    // 图片名称
                    String imgName = FilenameUtils.getName(originalFilename);
                    String iconName = appIconDTO.getName();
                    if (StringUtils.isBlank(iconName)) {
                        iconName = FilenameUtils.getName(originalFilename);
                    }
                    try {
                        Optional<AppIcon> appIconOptional = appIconService.findByNameAndColorType(imgName, colors[i]);
                        appIconService.save(iconName, appIconDTO.getCategory(), colors[i], appIconDTO.getRemark(),
                            file);
                        if (appIconOptional.isPresent()) {
                            msgBuilder.append("上传颜色：" + colors[i] + "成功,文件重名，图标已被覆盖!").append(";");
                        }
                    } catch (Y9BusinessException e) {
                        msgBuilder.append("上传颜色：" + colors[i] + "失败").append(";");
                    }
                }
            }
            if (msgBuilder.length() == 0) {
                msgBuilder.append("上传成功");
            }
            return Y9Result.success(msgBuilder.toString());
        } else {
            return Y9Result.failure("上传失败！请选择图标文件！");
        }
    }
}
