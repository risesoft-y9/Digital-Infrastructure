package net.risesoft.y9public.controller;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9.util.mime.MediaTypeUtils;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.exception.Y9FileErrorCodeEnum;
import net.risesoft.y9public.service.Y9FileStoreService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
public class Y9FileController {

    private final Y9FileStoreService y9FileStoreService;
    private final ServletContext servletContext;

    @RequestMapping(value = "/s/{realStoreFileName}")
    public void download(@PathVariable String realStoreFileName, HttpServletResponse response) throws Exception {
        String id = FilenameUtils.getBaseName(realStoreFileName);
        Y9FileStore y9FileStore = y9FileStoreService.getById(id);

        Y9Assert.notNull(y9FileStore, Y9FileErrorCodeEnum.FILE_NOT_FOUND, id);

        try (ServletOutputStream out = response.getOutputStream()) {
            response.setHeader("Content-disposition", ContentDispositionUtil.standardizeAttachment(y9FileStore.getFileName()));
            response.setContentType(MediaTypeUtils.getMediaTypeForFileName(servletContext, y9FileStore.getFileName()).toString());
            response.setContentLengthLong(y9FileStore.getFileSize());

            y9FileStoreService.downloadFileToOutputStream(id, out);
            out.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/s")
    public void download2(@RequestParam String realStoreFileName, HttpServletResponse response) throws Exception {
        download(realStoreFileName, response);
    }

    @RequestMapping(value = "/files/{realStoreFileName}")
    public void downloadFiles(@PathVariable String realStoreFileName, HttpServletResponse response) throws Exception {
        download(realStoreFileName, response);
    }

}
