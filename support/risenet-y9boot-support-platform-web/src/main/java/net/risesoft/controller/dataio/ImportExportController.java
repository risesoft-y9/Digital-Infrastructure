package net.risesoft.controller.dataio;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.dataio.JxlsUtil;
import net.risesoft.dataio.org.Y9OrgTreeDataHandler;
import net.risesoft.dataio.role.Y9RoleDataHandler;
import net.risesoft.dataio.system.Y9SystemDataHandler;
import net.risesoft.dataio.system.model.Y9AppExportModel;
import net.risesoft.dataio.system.model.Y9SystemExportModel;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.util.StringUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;

import cn.hutool.core.io.resource.ClassPathResource;

/**
 * 导入导出管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/impExp", produces = "application/json")
@Slf4j
public class ImportExportController {

    private final Y9OrgTreeDataHandler y9OrgTreeExcelDataHandler;
    private final Y9OrgTreeDataHandler y9OrgTreeXmlDataHandler;
    private final Y9RoleService y9RoleService;
    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9SystemService y9SystemService;
    private final Y9AppService y9AppService;
    private final Y9RoleDataHandler y9RoleDataHandler;
    private final Y9SystemDataHandler y9SystemDataHandler;

    public ImportExportController(
        @Qualifier("y9OrgTreeExcelDataHandler") Y9OrgTreeDataHandler y9OrgTreeExcelDataHandler,
        @Qualifier("y9OrgTreeXmlDataHandler") Y9OrgTreeDataHandler y9OrgTreeXmlDataHandler, Y9RoleService y9RoleService,
        CompositeOrgBaseService compositeOrgBaseService, Y9SystemService y9SystemService, Y9AppService y9AppService,
        Y9RoleDataHandler y9RoleDataHandler, Y9SystemDataHandler y9SystemDataHandler) {
        this.y9OrgTreeExcelDataHandler = y9OrgTreeExcelDataHandler;
        this.y9OrgTreeXmlDataHandler = y9OrgTreeXmlDataHandler;
        this.y9RoleService = y9RoleService;
        this.compositeOrgBaseService = compositeOrgBaseService;
        this.y9SystemService = y9SystemService;
        this.y9AppService = y9AppService;
        this.y9RoleDataHandler = y9RoleDataHandler;
        this.y9SystemDataHandler = y9SystemDataHandler;
    }

    /**
     * 导出应用JSON
     *
     * @param appId 应用id
     * @param response
     */
    @RiseLog(operationName = "导出应用JSON", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportAppJSON")
    public void exportAppJson(@RequestParam String appId, HttpServletResponse response) {
        Y9App y9App = y9AppService.getById(appId);
        Y9AppExportModel y9AppExportModel = y9SystemDataHandler.buildApp(appId);
        String jsonStr = Y9JsonUtil.writeValueAsStringWithDefaultPrettyPrinter(y9AppExportModel);
        try (OutputStream outStream = response.getOutputStream();
            InputStream in = new ByteArrayInputStream(jsonStr.getBytes(StandardCharsets.UTF_8))) {

            String filename = ContentDispositionUtil.standardizeAttachment(
                y9App.getName() + "-应用信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".json");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", filename);
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf, 0, 1024)) != -1) {
                outStream.write(buf, 0, len);
            }
            outStream.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导出组织架构XLS
     *
     * @param resourceId 资源id
     * @param response
     */
    @RiseLog(operationName = "导出组织架构XLS", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportOrgTreeXls")
    public void exportOrgTreeXls(@RequestParam String resourceId, HttpServletResponse response) {
        // 获取数据
        Map<String, Object> map = y9OrgTreeExcelDataHandler.xlsData(resourceId);
        Y9OrgBase base = compositeOrgBaseService.getOrgBase(resourceId);

        try (OutputStream outStream = response.getOutputStream();
            InputStream in = new ClassPathResource("/template/exportTemplate.xlsx").getStream()) {

            String filename = ContentDispositionUtil.standardizeAttachment(
                base.getName() + "-组织架构" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", filename);

            List<String> listSheetNames = new ArrayList<>();
            listSheetNames.add("组织机构");
            listSheetNames.add("部门");
            listSheetNames.add("人员");
            listSheetNames.add("用户组");
            listSheetNames.add("岗位");
            listSheetNames.add("角色");

            map.put("sheetNames", listSheetNames);
            map.put("listSheetNames", listSheetNames);
            JxlsUtil jxlsUtil = new JxlsUtil();
            jxlsUtil.exportExcel(in, outStream, map);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导出组织架构树XML
     *
     * @param orgBaseId 组织节点id
     *
     * @return
     */
    @RiseLog(operationName = "导出组织架构树XML", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportOrgTreeXml")
    public void exportOrgTreeXml(@RequestParam String orgBaseId, HttpServletResponse response) {
        String xmlString = y9OrgTreeXmlDataHandler.doExport(orgBaseId);
        xmlString = StringUtil.strChangeToXml(xmlString.getBytes(StandardCharsets.UTF_8));

        try (OutputStream outStream = response.getOutputStream();
            InputStream in = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))) {
            Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgBase(orgBaseId);

            String filename = ContentDispositionUtil.standardizeAttachment(
                y9OrgBase.getName() + "-组织架构-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xml");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", filename);

            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf, 0, 1024)) != -1) {
                outStream.write(buf, 0, len);
            }
            outStream.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }

    }

    /**
     * 导出人员XLS
     *
     * @param orgBaseId 组织机构id
     */
    @RiseLog(operationName = "导出人员XLS", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportOrgXls")
    public void exportOrgXls(@RequestParam String orgBaseId, HttpServletRequest request, HttpServletResponse response) {
        // 获取数据
        Map<String, Object> map = y9OrgTreeExcelDataHandler.xlsPersonData(orgBaseId);
        Y9OrgBase base = compositeOrgBaseService.getOrgBase(orgBaseId);

        try (OutputStream outStream = response.getOutputStream();
            InputStream in = new ClassPathResource("/template/exportSimpleTemplate.xlsx").getStream()) {
            String filename = ContentDispositionUtil.standardizeAttachment(
                base.getName() + "-组织架构" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", filename);

            JxlsUtil jxlsUtil = new JxlsUtil();
            jxlsUtil.exportExcel(in, outStream, map);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导出角色树XML
     *
     * @param resourceId 资源id
     */
    @RiseLog(operationName = "导出角色树XML", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportRoleXml")
    public void exportRoleXml(@RequestParam String resourceId, HttpServletResponse response) {
        String xmlString = y9RoleDataHandler.doExport(resourceId);
        xmlString = StringUtil.strChangeToXml(xmlString.getBytes(StandardCharsets.UTF_8));

        try (OutputStream outStream = response.getOutputStream();
            InputStream in = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))) {
            Y9Role y9Role = y9RoleService.findById(resourceId);
            String filename = ContentDispositionUtil.standardizeAttachment(
                y9Role.getName() + "-角色信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xml");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", filename);
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf, 0, 1024)) != -1) {
                outStream.write(buf, 0, len);
            }
            outStream.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导出系统JSON
     *
     * @param systemId 系统id
     * @param response
     */
    @RiseLog(operationName = "导出系统JSON", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportSystemJSON")
    public void exportSystemJson(@RequestParam String systemId, HttpServletResponse response) {
        Y9System y9System = y9SystemService.getById(systemId);
        Y9SystemExportModel y9SystemExportModel = y9SystemDataHandler.buildSystem(systemId);
        String jsonStr = Y9JsonUtil.writeValueAsStringWithDefaultPrettyPrinter(y9SystemExportModel);

        try (OutputStream outStream = response.getOutputStream();
            InputStream in = new ByteArrayInputStream(jsonStr.getBytes(StandardCharsets.UTF_8))) {
            String filename = ContentDispositionUtil.standardizeAttachment(
                y9System.getCnName() + "-系统信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".json");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", filename);
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf, 0, 1024)) != -1) {
                outStream.write(buf, 0, len);
            }
            outStream.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导入应用JSON
     *
     * @param file 文件
     * @return
     * @throws IOException
     */
    @RiseLog(operationName = "导入应用JSON", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/importAppJSON")
    public Y9Result<Object> importAppJson(@RequestParam MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        Y9AppExportModel y9AppExportModel = Y9JsonUtil.readValue(content, Y9AppExportModel.class);
        y9SystemDataHandler.importApp(y9AppExportModel);
        return Y9Result.success(null, "导入成功");
    }

    /**
     * 上传组织机构XLS
     *
     * @param file 文件
     * @param orgId 组织id
     * @return
     */
    @RiseLog(operationName = "上传组织机构XLS", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/importOrgTreeXls")
    public Y9Result<Object> importOrgTreeXls(@RequestParam MultipartFile file, @RequestParam String orgId) {
        try (
            InputStream xmlfis =
                new BufferedInputStream(this.getClass().getResourceAsStream("/template/xmlconfig.xml"));
            InputStream datafis = file.getInputStream();) {
            return y9OrgTreeExcelDataHandler.impXlsData(datafis, xmlfis, orgId);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("上传失败:" + e.getMessage());
        }
    }

    /**
     * 导入组织机构信息XML
     *
     * @param upload 文件
     * @return
     * @throws IOException
     */
    @RiseLog(operationName = "导入组织机构信息XML", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/importOrgXml")
    public Y9Result<Object> importOrgXml(@RequestParam MultipartFile upload) throws IOException {
        return y9OrgTreeXmlDataHandler.doImport(upload.getInputStream());
    }

    /**
     * 导入系统JSON
     *
     * @param file 文件
     * @return
     * @throws IOException
     */
    @RiseLog(operationName = "导入系统JSON", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/importSystemJSON")
    public Y9Result<Object> importSystemJson(@RequestParam MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        Y9SystemExportModel y9SystemExportModel = Y9JsonUtil.readValue(content, Y9SystemExportModel.class);
        y9SystemDataHandler.importSystem(y9SystemExportModel);
        return Y9Result.success(null, "导入成功");
    }

}
