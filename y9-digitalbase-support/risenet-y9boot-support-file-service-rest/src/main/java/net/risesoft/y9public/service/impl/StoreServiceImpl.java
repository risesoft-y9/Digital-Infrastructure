package net.risesoft.y9public.service.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.enums.FileStoreTypeEnum;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9public.service.StoreService;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    protected Y9Properties y9Config;

    @Override
    public void deleteFile(String fullPath, String realFileName) throws Exception {
        String fileManagerUrl = y9Config.getFeature().getFile().getRest().getFileManagerUrl();
        String destination = fileManagerUrl + "/rest/deleteFile";
        HttpRequest httpRequest = HttpRequest.post(destination).form("fullPath", fullPath, "fileName", realFileName);
        HttpResponse httpResponse = httpRequest.send();
        httpResponse.close();
    }

    @Override
    public FileStoreTypeEnum getStoreType() {
        return FileStoreTypeEnum.REST;
    }

    @Override
    public byte[] retrieveFileBytes(String fullPath, String realFileName) throws Exception {
        String fileManagerUrl = y9Config.getFeature().getFile().getRest().getFileManagerUrl();
        String destination = fileManagerUrl + "/rest/retrieveFileStream";
        HttpRequest httpRequest = HttpRequest.post(destination).form("fullPath", fullPath, "fileName", realFileName);
        HttpResponse httpResponse = httpRequest.send();
        byte[] bytes = httpResponse.bodyBytes();
        httpResponse.close();
        return bytes;
    }

    @Override
    public void retrieveFileStream(String fullPath, String realFileName, OutputStream outputStream) throws Exception {
        byte[] bytes = this.retrieveFileBytes(fullPath, realFileName);
        IOUtils.write(bytes, outputStream);
    }

    @Override
    public void storeFile(String fullPath, String realFileName, byte[] bytes) throws Exception {
        String tempDir = System.getProperty("java.io.tmpdir");
        File tempFile = new File(tempDir, realFileName);
        FileUtils.writeByteArrayToFile(tempFile, bytes);

        String fileManagerUrl = y9Config.getFeature().getFile().getRest().getFileManagerUrl();
        String destination = fileManagerUrl + "/rest/storeFile";
        HttpRequest httpRequest = HttpRequest.post(destination).form("fullPath", fullPath, "fileName", realFileName,
            "multipartFile", tempFile);
        HttpResponse httpResponse = httpRequest.send();
        httpResponse.close();

        tempFile.delete();
    }

    @Override
    public void storeFile(String fullPath, String realFileName, InputStream inputStream) throws Exception {
        this.storeFile(fullPath, realFileName, IOUtils.toByteArray(inputStream));
    }
}
