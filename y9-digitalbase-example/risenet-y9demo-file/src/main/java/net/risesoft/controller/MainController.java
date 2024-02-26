package net.risesoft.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@RestController
@RequestMapping(value = "/main")
public class MainController {

    Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    /**
     * 上传文件
     * 
     * @param file
     */
    @PostMapping(value = "/uploadFile")
    public void uploadFile(MultipartFile file) {
        try {
            String fullPath = "/aaaa/bbbb";
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, file.getOriginalFilename());
            log.info("fileStoreId:{}", y9FileStore.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件唯一标示下载文件
     * 
     * @param fileStoreId
     * @param response
     * @param request
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam String fileStoreId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
            String title = y9FileStore.getFileName();
            title = java.net.URLEncoder.encode(title, StandardCharsets.UTF_8);
            title = StringUtils.replace(title, "+", "%20");// 替换空格
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + title + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件唯一标示删除文件
     * 
     * @param fileStoreId
     */
    @RequestMapping(value = "/deleteFile")
    public void deleteFile(@RequestParam String fileStoreId) {
        try {
            y9FileStoreService.deleteFile(fileStoreId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
