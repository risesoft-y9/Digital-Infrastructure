package net.risesoft.y9public.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.mime.DownloadFileNameUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.exception.Y9FileErrorCodeEnum;
import net.risesoft.y9public.service.Y9FileStoreService;

@Controller
@Slf4j
public class Y9FileController {

    @Resource(name = "y9FileStoreService")
    private Y9FileStoreService y9FileStoreService;

    @RequestMapping(value = "/s/{realStoreFileName}")
    public void download(@PathVariable String realStoreFileName, HttpServletResponse response) throws Exception {
        String id = FilenameUtils.getBaseName(realStoreFileName);
        Y9FileStore y9FileStore = y9FileStoreService.getById(id);

        Y9Assert.notNull(y9FileStore, Y9FileErrorCodeEnum.FILE_NOT_FOUND, id);

        try (ServletOutputStream out = response.getOutputStream()) {
            response.setHeader("Content-disposition", DownloadFileNameUtil.standardize(y9FileStore.getFileName()));
            response.setContentType("application/octet-stream");

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
