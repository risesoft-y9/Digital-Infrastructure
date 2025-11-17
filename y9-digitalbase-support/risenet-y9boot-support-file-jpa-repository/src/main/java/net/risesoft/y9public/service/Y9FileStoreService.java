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

    Y9FileStore uploadFile(byte[] bytes, String customPath, String fileName) throws Exception;

    Y9FileStore uploadFile(File file, String customPath, String fileName) throws Exception;

    Y9FileStore uploadFile(MultipartFile multipartFile, String customPath, String fileName) throws Exception;

    Y9FileStore uploadFile(InputStream inputStream, String customPath, String fileName) throws Exception;

    Y9FileStore uploadFileReplace(String y9FileStoreId, byte[] bytes) throws Exception;

    Y9FileStore uploadFileReplace(String y9FileStoreId, File file) throws Exception;

    Y9FileStore uploadFileReplace(String y9FileStoreId, MultipartFile multipartFile) throws Exception;

    Y9FileStore uploadFileReplace(String y9FileStoreId, InputStream inputStream) throws Exception;
}
