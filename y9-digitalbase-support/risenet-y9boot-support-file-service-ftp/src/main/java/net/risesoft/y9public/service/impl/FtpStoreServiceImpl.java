package net.risesoft.y9public.service.impl;

import java.io.InputStream;
import java.io.OutputStream;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.FileStoreTypeEnum;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.ftp.FtpClientHelper;
import net.risesoft.y9public.service.StoreService;

@RequiredArgsConstructor
public class FtpStoreServiceImpl implements StoreService {

    private final FtpClientHelper ftpClientHelper;

    @Override
    public void deleteFile(String fullPath, String realFileName) throws Exception {
        String fullPathAndRealFileName = Y9FileStore.buildFullPath(fullPath, realFileName);
        ftpClientHelper.deleteFile(fullPathAndRealFileName);
    }

    @Override
    public FileStoreTypeEnum getStoreType() {
        return FileStoreTypeEnum.FTP;
    }

    @Override
    public byte[] retrieveFileBytes(String fullPath, String realFileName) throws Exception {
        String fullPathAndRealFileName = Y9FileStore.buildFullPath(fullPath, realFileName);
        return ftpClientHelper.retrieveFileBytes(fullPathAndRealFileName);
    }

    @Override
    public void retrieveFileStream(String fullPath, String realFileName, OutputStream outputStream) throws Exception {
        String fullPathAndRealFileName = Y9FileStore.buildFullPath(fullPath, realFileName);
        ftpClientHelper.retrieveFileStream(fullPathAndRealFileName, outputStream);
    }

    @Override
    public void storeFile(String fullPath, String realFileName, byte[] bytes) throws Exception {
        ftpClientHelper.storeFile(fullPath, realFileName, bytes);
    }

    @Override
    public void storeFile(String fullPath, String realFileName, InputStream inputStream) throws Exception {
        ftpClientHelper.storeFile(fullPath, realFileName, inputStream);
    }
}
