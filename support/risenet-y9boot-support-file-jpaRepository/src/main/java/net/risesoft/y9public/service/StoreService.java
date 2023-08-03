package net.risesoft.y9public.service;

import java.io.InputStream;
import java.io.OutputStream;

public interface StoreService {

    void deleteFile(String fullPath, String realFileName) throws Exception;

    Integer getStoreType();

    byte[] retrieveFileBytes(String fullPath, String realFileName) throws Exception;

    void retrieveFileStream(String fullPath, String realFileName, OutputStream outputStream) throws Exception;

    void storeFile(String fullPath, String realFileName, byte[] bytes) throws Exception;

    void storeFile(String fullPath, String realFileName, InputStream inputStream) throws Exception;
}
