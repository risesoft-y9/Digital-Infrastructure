package net.risesoft.y9public.service;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.y9public.entity.Y9FileStore;

public interface Y9FileStoreService {

    boolean deleteFile(String y9FileStoreId);

    byte[] downloadFileToBytes(String y9FileStoreId) throws Exception;
    void downloadFileToOutputStream(String y9FileStoreId, OutputStream outputStream) throws Exception;
    String downloadFileToString(String y9FileStoreId) throws Exception;
    
    Y9FileStore getById(String y9FileStoreId);

    Y9FileStore uploadFile(byte[] bytes, String fullPath, String fileName) throws Exception;
    Y9FileStore uploadFile(File file, String fullPath, String fileName) throws Exception;
    Y9FileStore uploadFile(MultipartFile multipartFile, String fullPath, String fileName) throws Exception;
    Y9FileStore uploadFile(InputStream inputStream, String fullPath, String fileName) throws Exception;
    
    Y9FileStore uploadFileAsync(byte[] bytes, String fullPath, String fileName) throws Exception;
    Y9FileStore uploadFileAsync(File file, String fullPath, String fileName) throws Exception;
    Y9FileStore uploadFileAsync(MultipartFile multipartFile, String fullPath, String fileName) throws Exception;
    Y9FileStore uploadFileAsync(InputStream inputStream, String fullPath, String fileName) throws Exception;

    Y9FileStore uploadFileReplace(byte[] bytes, String y9FileStoreId) throws Exception;
    Y9FileStore uploadFileReplace(File file, String y9FileStoreId) throws Exception;
    Y9FileStore uploadFileReplace(MultipartFile multipartFile, String y9FileStoreId) throws Exception;
    Y9FileStore uploadFileReplace(InputStream inputStream, String y9FileStoreId) throws Exception;
}
