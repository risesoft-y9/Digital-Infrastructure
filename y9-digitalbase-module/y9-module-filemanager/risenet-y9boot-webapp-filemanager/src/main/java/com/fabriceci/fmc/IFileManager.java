package com.fabriceci.fmc;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fabriceci.fmc.error.FileManagerException;
import com.fabriceci.fmc.model.FileData;
import com.fabriceci.fmc.model.InitiateData;

public interface IFileManager {

    FileData actionAddFolder(String path, String name) throws FileManagerException;

    // GET

    FileData actionCopy(String sourcePath, String targetPath) throws FileManagerException;

    FileData actionDelete(String path) throws FileManagerException;

    FileData actionDownload(HttpServletResponse response, String path) throws FileManagerException;

    List<FileData> actionExtract(String sourcePath, String targetPath) throws FileManagerException;

    FileData actionGetImage(HttpServletResponse response, String path, Boolean thumbnail) throws FileManagerException;

    FileData actionGetInfo(String path) throws FileManagerException;

    InitiateData actionInitiate() throws FileManagerException;

    // TO test :

    FileData actionMove(String sourcePath, String targetPath) throws FileManagerException;

    FileData actionReadFile(HttpServletRequest request, HttpServletResponse response, String path)
        throws FileManagerException;

    List<FileData> actionReadFolder(String path, String type) throws FileManagerException;

    FileData actionRename(String sourcePath, String targetPath) throws FileManagerException;

    FileData actionSaveFile(String pathParam, String contentParam) throws FileManagerException;

    Object actionSeekFolder(String folderPath, String term) throws FileManagerException;

    FileData actionSummarize() throws FileManagerException;

    List<FileData> actionUpload(HttpServletRequest request, String path) throws FileManagerException;

    void handleRequest(HttpServletRequest request, HttpServletResponse response);

}
