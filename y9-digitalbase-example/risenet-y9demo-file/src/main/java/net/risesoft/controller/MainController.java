package net.risesoft.controller;

import java.io.OutputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@RestController
@RequestMapping(value = "/main")
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final Y9FileStoreService y9FileStoreService;

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

            response.setContentType("text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", ContentDispositionUtil.standardizeAttachment(title));
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @param file
     */
    @PostMapping(value = "/uploadFile")
    public void uploadFile(MultipartFile file) {
        try {
            String fullPath = Y9FileStore.buildPath("aaa", "bbb");
            Y9FileStore y9FileStore =
                y9FileStoreService.uploadFile(file.getInputStream(), fullPath, file.getOriginalFilename());
            LOGGER.info("fileStoreId:{}", y9FileStore.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
