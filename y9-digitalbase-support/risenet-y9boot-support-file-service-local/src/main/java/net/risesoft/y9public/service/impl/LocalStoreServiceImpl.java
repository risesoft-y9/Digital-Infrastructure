package net.risesoft.y9public.service.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.FileStoreTypeEnum;
import net.risesoft.y9.configuration.feature.file.local.Y9LocalProperties;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.StoreService;

@RequiredArgsConstructor
public class LocalStoreServiceImpl implements StoreService {

    private final Y9LocalProperties y9LocalProperties;

    @Override
    public void deleteFile(String fullPath, String realFileName) throws Exception {
        File file = new File(Y9FileStore.buildPath(y9LocalProperties.getBasePath(), fullPath), realFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public FileStoreTypeEnum getStoreType() {
        return FileStoreTypeEnum.LOCAL;
    }

    @Override
    public byte[] retrieveFileBytes(String fullPath, String realFileName) throws Exception {
        File file = new File(Y9FileStore.buildPath(y9LocalProperties.getBasePath(), fullPath), realFileName);
        if (file.exists()) {
            return FileUtils.readFileToByteArray(file);
        }
        return new byte[0];
    }

    @Override
    public void retrieveFileStream(String fullPath, String realFileName, OutputStream outputStream) throws Exception {
        IOUtils.write(this.retrieveFileBytes(fullPath, realFileName), outputStream);
    }

    @Override
    public void storeFile(String fullPath, String realFileName, byte[] bytes) throws Exception {
        File parentFolder = new File(Y9FileStore.buildPath(y9LocalProperties.getBasePath(), fullPath));
        parentFolder.mkdirs();
        File newFile = new File(parentFolder, realFileName);
        FileUtils.writeByteArrayToFile(newFile, bytes);
    }

    @Override
    public void storeFile(String fullPath, String realFileName, InputStream inputStream) throws Exception {
        this.storeFile(fullPath, realFileName, IOUtils.toByteArray(inputStream));
    }
}
