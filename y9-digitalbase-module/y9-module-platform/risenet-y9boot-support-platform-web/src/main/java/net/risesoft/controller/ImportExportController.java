package net.risesoft.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.dataio.org.Y9OrgTreeDataHandler;
import net.risesoft.dataio.org.Y9PersonDataHandler;
import net.risesoft.dataio.org.model.OrganizationJsonModel;
import net.risesoft.dataio.system.SystemDataHandler;
import net.risesoft.dataio.system.model.AppJsonModel;
import net.risesoft.dataio.system.model.SystemJsonModel;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.resource.App;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9.util.mime.MediaTypeUtil;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9SystemService;

/**
 * 导入导出管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/impExp", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@Validated
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
public class ImportExportController {

    private final Y9PersonDataHandler y9PersonDataHandler;
    private final SystemDataHandler systemDataHandler;
    private final Y9OrgTreeDataHandler y9OrgTreeDataHandler;

    private final Y9SystemService y9SystemService;
    private final Y9AppService y9AppService;
    private final CompositeOrgBaseService compositeOrgBaseService;

    private final ServletContext servletContext;

    /**
     * 导出应用JSON
     *
     * @param appId 应用id
     * @param response 响应
     */
    @RiseLog(operationName = "导出应用JSON", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportAppJSON")
    public void exportAppJson(@RequestParam @NotBlank String appId, HttpServletResponse response) {
        try (OutputStream outStream = response.getOutputStream()) {

            App app = y9AppService.getById(appId);
            String filename =
                app.getName() + "-应用信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".json";

            response.setHeader("Content-disposition", ContentDispositionUtil.standardizeAttachment(filename));
            response.setContentType(MediaTypeUtil.getMediaTypeForFileName(servletContext, filename).toString());

            systemDataHandler.exportApp(appId, outStream);

        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @RiseLog(operationName = "导出组织架构JSON", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportOrgTreeJson")
    public void exportOrgTreeJson(@RequestParam @NotBlank String orgId, HttpServletResponse response) {
        try (OutputStream outStream = response.getOutputStream()) {
            OrgUnit orgUnit = compositeOrgBaseService.getOrgUnit(orgId);
            String filename =
                orgUnit.getName() + "-组织架构-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".json";
            response.setContentType(MediaTypeUtil.getMediaTypeForFileName(servletContext, filename).toString());
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(filename));

            y9OrgTreeDataHandler.exportOrgTree(orgId, outStream);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导出人员XLS
     *
     * @param orgBaseId 组织机构id
     * @param response 响应
     */
    @RiseLog(operationName = "导出人员XLS", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportPersonXls")
    public void exportPersonXls(@RequestParam @NotBlank String orgBaseId, HttpServletResponse response) {
        try (OutputStream outStream = response.getOutputStream()) {
            OrgUnit orgUnit = compositeOrgBaseService.getOrgUnit(orgBaseId);
            String filename =
                orgUnit.getName() + "-组织架构-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            response.setContentType(MediaTypeUtil.getMediaTypeForFileName(servletContext, filename).toString());
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(filename));

            y9PersonDataHandler.exportPerson(outStream, orgBaseId);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导出系统JSON
     *
     * @param systemId 系统id
     * @param response 响应
     */
    @RiseLog(operationName = "导出系统JSON", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportSystemJSON")
    public void exportSystemJson(@RequestParam @NotBlank String systemId, HttpServletResponse response) {
        try (OutputStream outStream = response.getOutputStream()) {

            System system = y9SystemService.getById(systemId);
            String filename =
                system.getCnName() + "-系统信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".json";
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(filename));
            response.setContentType(MediaTypeUtil.getMediaTypeForFileName(servletContext, filename).toString());

            systemDataHandler.exportSystem(systemId, outStream);

        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导入应用JSON
     * 
     * @param file 文件
     * @param systemId 系统id
     * @return {@code Y9Result<Object>}
     * @throws IOException IO异常
     */
    @RiseLog(operationName = "导入应用JSON", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/importAppJSON")
    public Y9Result<Object> importAppJson(@RequestParam MultipartFile file, String systemId) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        AppJsonModel appJsonModel = Y9JsonUtil.readValue(content, AppJsonModel.class);
        systemDataHandler.importApp(appJsonModel, systemId);
        return Y9Result.success(null, "导入成功");
    }

    /**
     * 导入组织架构JSON
     *
     * @param file 文件
     * @return {@code Y9Result<Object>}
     * @throws IOException IO异常
     */
    @RiseLog(operationName = "导入组织架构JSON", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/importOrgTreeJSON")
    public Y9Result<Object> importOrgTreeJson(@RequestParam MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        OrganizationJsonModel organizationJsonModel = Y9JsonUtil.readValue(content, OrganizationJsonModel.class);
        y9OrgTreeDataHandler.importOrgTree(organizationJsonModel);
        return Y9Result.success(null, "导入成功");
    }

    /**
     * 导入人员 xls
     *
     * @param file 文件
     * @param orgId 组织id
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "导入人员 xls", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/importPersonXls")
    public Y9Result<Object> importPersonXls(@RequestParam MultipartFile file, @RequestParam String orgId) {
        try (InputStream fileInputStream = file.getInputStream()) {
            return y9PersonDataHandler.importPerson(fileInputStream, orgId);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("上传失败:" + e.getMessage());
        }
    }

    /**
     * 导入系统JSON
     *
     * @param file 文件
     * @return {@code Y9Result<Object>}
     * @throws IOException IO异常
     */
    @RiseLog(operationName = "导入系统JSON", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/importSystemJSON")
    public Y9Result<Object> importSystemJson(@RequestParam MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        SystemJsonModel systemJsonModel = Y9JsonUtil.readValue(content, SystemJsonModel.class);
        systemDataHandler.importSystem(systemJsonModel);
        return Y9Result.success(null, "导入成功");
    }

}
