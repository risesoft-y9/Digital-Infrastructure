package net.risesoft.y9public.controller;

import java.io.File;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.repository.Y9FileStoreRepository;

@Controller
@Slf4j
@RequiredArgsConstructor
public class Y9FileController {

    private String fileStoreRoot = "d:/y9config/y9filestore";

    private final Y9FileStoreRepository fileRepository;

    private final Environment environment;

    @RequestMapping(value = "/rest/deleteFile")
    @ResponseBody
    public String deleteFile(String fullPath, String fileName, HttpServletResponse response) throws Exception {
        try {
            File path = new File(this.fileStoreRoot + fullPath);
            path.mkdirs();
            File file = new File(path, fileName);
            if (file.exists()) {
                file.delete();
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "failure";
    }

    @RequestMapping(value = "/s/{realStoreFileName}")
    public void download(@PathVariable String realStoreFileName, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        downloadFile(realStoreFileName, request, response);
    }

    @RequestMapping(value = "/s")
    public void download2(@RequestParam String realStoreFileName, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        downloadFile(realStoreFileName, request, response);
    }

    /**
     * 附件下载
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/file/{realStoreFileName}")
    public void downloadFile(@PathVariable String realStoreFileName, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        ServletOutputStream out = response.getOutputStream();
        try {
            String id = realStoreFileName;
            int i = id.indexOf(".");
            if (i != -1) {
                id = id.substring(0, i);
            }
            Y9FileStore f = fileRepository.findById(id).orElse(null);
            if (f != null) {
                String fileName = f.getFileName();
                String fullPath = f.getFullPath();

                File file = new File(this.fileStoreRoot + fullPath + "/" + f.getRealFileName());
                if (file.exists()) {
                    String userAgent = request.getHeader("User-Agent");
                    if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                        fileName = URLEncoder.encode(fileName, "UTF-8");
                        fileName = fileName.replaceAll("\\+", "%20");
                    } else {
                        fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
                    }

                    response.reset();
                    response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
                    response.setHeader("Content-type", "text/html;charset=UTF-8");
                    response.setContentType("application/octet-stream");
                    // byte[] bytes = FileUtils.readFileToByteArray(file);
                    // IOUtils.write(bytes, out);
                    FileUtils.copyFile(file, out);
                    return;
                }
            }

            response.reset();
            String err = "文件不存在或路径不正确";
            out.write(err.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();

            response.reset();
            String err = "错误信息：" + e.getMessage();
            out.write(err.getBytes("UTF-8"));
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "/files")
    public void downloadFile2(@RequestParam String realStoreFileName, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        downloadFile(realStoreFileName, request, response);
    }

    @RequestMapping(value = "/files/{realStoreFileName}")
    public void downloadFiles(@PathVariable String realStoreFileName, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        download(realStoreFileName, request, response);
    }

    @PostConstruct
    public void init() {
        this.fileStoreRoot = environment.getProperty("y9.app.fileManager.fileRoot", "d:/y9config/y9filestore");
    }

    @RequestMapping(value = "/rest/retrieveFileStream")
    public void retrieveFileStream(String fullPath, String fileName, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        ServletOutputStream out = response.getOutputStream();
        try {
            File path = new File(this.fileStoreRoot + fullPath);
            File file = new File(path, fileName);
            if (file.exists()) {
                String userAgent = request.getHeader("User-Agent");
                if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                    fileName = URLEncoder.encode(fileName, "UTF-8");
                    fileName = fileName.replaceAll("\\+", "%20");
                } else {
                    fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
                }

                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");

                // byte[] bytes = FileUtils.readFileToByteArray(file);
                // IOUtils.write(bytes, out);
                FileUtils.copyFile(file, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    @PostMapping(value = "/rest/storeFile")
    @ResponseBody
    public String storeFile(MultipartFile multipartFile, String fullPath, String fileName) throws Exception {
        try {
            File path = new File(this.fileStoreRoot + fullPath);
            path.mkdirs();
            File file = new File(path, fileName);
            multipartFile.transferTo(file);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "failure";
    }

}
