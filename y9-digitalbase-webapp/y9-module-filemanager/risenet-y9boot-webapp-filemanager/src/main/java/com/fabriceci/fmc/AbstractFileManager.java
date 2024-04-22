package com.fabriceci.fmc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fabriceci.fmc.error.ClientErrorMessage;
import com.fabriceci.fmc.error.FileManagerException;
import com.fabriceci.fmc.model.ConfigExtensions;
import com.fabriceci.fmc.model.ConfigRoot;
import com.fabriceci.fmc.model.ConfigSecurity;
import com.fabriceci.fmc.model.ConfigUpload;
import com.fabriceci.fmc.model.ErrorItem;
import com.fabriceci.fmc.model.ErrorResponse;
import com.fabriceci.fmc.model.FileData;
import com.fabriceci.fmc.model.InitiateAttributes;
import com.fabriceci.fmc.model.InitiateData;
import com.fabriceci.fmc.model.SuccessResponse;
import com.fabriceci.fmc.util.FileUtils;
import com.fabriceci.fmc.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractFileManager implements IFileManager {

    private final static String CONFIG_DEFAULT_PROPERTIES = "/richFileManager/filemanager.config.default.properties";
    private final static String CONFIG_CUSTOM_PROPERTIES = "/richFileManager/filemanager.config.properties";
    protected final static String LANG_FILE = "/richFileManager/filemanager.lang.zh-CN.properties";
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private static String cleanPath(String path) {
        if (path == null) {
            return null;
        }
        return path.replace("//", "/").replace("..", "");
    }

    protected final Logger logger = LoggerFactory.getLogger(AbstractFileManager.class);
    protected boolean readOnly = false;

    protected Properties propertiesConfig = new Properties();

    public AbstractFileManager() {
        this(null);
    }

    public AbstractFileManager(Map<String, String> options) {
        // load server properties
        InputStream tempLoadIS = null;

        // load default config file
        tempLoadIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_DEFAULT_PROPERTIES);
        if (tempLoadIS == null) {
            tempLoadIS = this.getClass().getResourceAsStream(CONFIG_DEFAULT_PROPERTIES);
        }
        try {
            if (tempLoadIS != null) {
                propertiesConfig.load(tempLoadIS);
            }
        } catch (IOException ignored) {
        }

        try {
            if (tempLoadIS != null) {
                tempLoadIS.close();
            }
        } catch (IOException ignored) {
        }

        // load custom config file if exists
        tempLoadIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_CUSTOM_PROPERTIES);
        if (tempLoadIS == null) {
            tempLoadIS = this.getClass().getResourceAsStream(CONFIG_CUSTOM_PROPERTIES);
        }
        if (tempLoadIS != null) {
            Properties customConfig = new Properties();
            try {
                customConfig.load(tempLoadIS);
            } catch (IOException ignored) {
            }

            propertiesConfig.putAll(customConfig);
            try {
                tempLoadIS.close();
            } catch (IOException ignored) {
            }
        }

        if (options != null && !options.isEmpty()) {
            propertiesConfig.putAll(options);
        }

        readOnly = Boolean.parseBoolean(propertiesConfig.getProperty("readOnly"));
    }

    @Override
    public FileData actionAddFolder(String path, String name) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileData actionCopy(String sourcePath, String targetPath) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileData actionDelete(String path) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileData actionDownload(HttpServletResponse response, String path) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FileData> actionExtract(String sourcePath, String targetPath) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileData actionGetImage(HttpServletResponse response, String path, Boolean thumbnail)
        throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileData actionGetInfo(String path) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InitiateData actionInitiate() throws FileManagerException {

        ConfigUpload configUpload = new ConfigUpload();
        configUpload.setFileSizeLimit(Long.parseLong(propertiesConfig.getProperty("upload.fileSizeLimit")));

        ConfigExtensions configExtensions = new ConfigExtensions();
        boolean policyAllow = Boolean.parseBoolean(propertiesConfig.getProperty("extensions.policy.allow"));
        configExtensions.setPolicy(policyAllow ? "ALLOW_LIST" : "DISALLOW_LIST");
        configExtensions.setRestrictions(propertiesConfig.getProperty("extensions.restrictions").split(","));

        ConfigSecurity configSecurity = new ConfigSecurity();
        configSecurity.setReadOnly(readOnly);
        configSecurity.setExtensions(configExtensions);

        ConfigRoot configRoot = new ConfigRoot();
        configRoot.setSecurity(configSecurity);
        configRoot.setUpload(configUpload);

        InitiateAttributes initiateAttributes = new InitiateAttributes();
        initiateAttributes.setConfig(configRoot);

        InitiateData initiateData = new InitiateData();
        initiateData.setAttributes(initiateAttributes);

        return initiateData;
    }

    @Override
    public FileData actionMove(String sourcePath, String targetPath) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileData actionReadFile(HttpServletRequest request, HttpServletResponse response, String path)
        throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FileData> actionReadFolder(String path, String type) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileData actionRename(String sourcePath, String targetPath) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileData actionSaveFile(String pathParam, String contentParam) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object actionSeekFolder(String folderPath, String term) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileData actionSummarize() throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FileData> actionUpload(HttpServletRequest request, String path) throws FileManagerException {
        throw new UnsupportedOperationException();
    }

    protected void checkPath(File file) throws FileManagerException {
        checkPath(file, null);
    }

    protected void checkPath(File file, Boolean isDir) throws FileManagerException {
        if (!file.exists()) {
            if (file.isDirectory()) {
                throw new FileManagerException(ClientErrorMessage.DIRECTORY_NOT_EXIST,
                    Collections.singletonList(file.getName()));
            } else {
                throw new FileManagerException(ClientErrorMessage.FILE_DOES_NOT_EXIST,
                    Collections.singletonList(file.getName()));
            }
        }

        if (isDir != null) {
            if (file.isDirectory() && !isDir) {
                throw new FileManagerException(ClientErrorMessage.INVALID_FILE_TYPE);
            }
        }
    }

    protected void checkReadPermission(File file) throws FileManagerException {

        if (!file.canRead()) {
            throw new FileManagerException(ClientErrorMessage.NOT_ALLOWED_SYSTEM);
        }
    }

    protected void checkRestrictions(File file) throws FileManagerException {
        checkRestrictions(file.getName(), FileUtils.isDirectory(file));
    }

    private void checkRestrictions(String name, boolean isDir) throws FileManagerException {
        if (!isDir) {
            if (!isAllowedFileExtension(name)) {
                throw new FileManagerException(ClientErrorMessage.FORBIDDEN_NAME, Collections.singletonList(name));
            }
        }

        if (!isAllowedPattern(name, isDir)) {
            throw new FileManagerException(ClientErrorMessage.INVALID_FILE_TYPE, Collections.singletonList(name));
        }
    }

    protected void checkWritePermission(File file) throws FileManagerException {

        if (readOnly) {
            throw new FileManagerException(ClientErrorMessage.NOT_ALLOWED);
        }

        // check system permission
        if (!file.canWrite()) {
            throw new FileManagerException(ClientErrorMessage.NOT_ALLOWED_SYSTEM);
        }
    }

    private void generateErrorResponse(HttpServletResponse response, String message, List<String> arguments) {
        response.setStatus(500);
        response.addHeader("Content-Type", "application/json; charset=utf-8");

        Gson gson = new GsonBuilder().create();

        ErrorItem errorItem = new ErrorItem(message, arguments);

        try {
            response.getWriter().write(gson.toJson(new ErrorResponse(errorItem)));
        } catch (IOException ignore) {
        }
    }

    private void generateResponse(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(200);
        response.addHeader("Content-Type", "application/json; charset=utf-8");

        Gson gson = new GsonBuilder().create();

        response.getWriter().write(gson.toJson(new SuccessResponse(data)));
    }

    protected final BufferedImage generateThumbnail(BufferedImage source) {
        return Scalr.resize(source, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH,
            Integer.parseInt(propertiesConfig.getProperty("images.thumbnail.maxWidth")),
            Integer.parseInt(propertiesConfig.getProperty("images.thumbnail.maxHeight")), Scalr.OP_ANTIALIAS);
    }

    public Properties getPropertiesConfig() {
        return propertiesConfig;
    }

    public final void handleRequest(HttpServletRequest request, HttpServletResponse response) {

        // baseUrl = ServletUtils.getBaseUrl(request);

        final String method = request.getMethod();
        final String mode = request.getParameter("mode");
        final String pathParam = cleanPath(request.getParameter("path"));
        String sourcePath = null;
        String targetPath = null;

        Object responseData = null;
        response.setStatus(200);

        try {
            if (StringUtils.isEmpty(mode)) {
                generateResponse(response, ClientErrorMessage.MODE_ERROR);
                return;
            }

            if (method.equals("GET") || method.equals("HEAD")) {
                switch (mode) {
                    default:
                        throw new FileManagerException(ClientErrorMessage.MODE_ERROR);
                    case "initiate":
                        responseData = actionInitiate();
                        break;
                    case "getinfo":
                        if (!StringUtils.isEmpty(pathParam)) {
                            responseData = actionGetInfo(pathParam);
                        }
                        break;
                    case "readfolder":
                        final String typeParam = request.getParameter("type");
                        if (!StringUtils.isEmpty(pathParam)) {
                            responseData = actionReadFolder(pathParam, typeParam);
                        }
                        break;
                    case "seekfolder":
                        final String searchTerm = request.getParameter("string");
                        if (!StringUtils.isEmpty(pathParam) && !StringUtils.isEmpty(searchTerm)) {
                            responseData = actionSeekFolder(pathParam, searchTerm);
                        }
                        break;
                    case "rename":
                        sourcePath = cleanPath(request.getParameter("old"));
                        targetPath = cleanPath(request.getParameter("new"));
                        if (!StringUtils.isEmpty(sourcePath) && !StringUtils.isEmpty(targetPath)) {
                            responseData = actionRename(sourcePath, targetPath);
                        }
                        break;
                    case "copy":
                        sourcePath = cleanPath(request.getParameter("source"));
                        targetPath = cleanPath(request.getParameter("target"));
                        if (!StringUtils.isEmpty(sourcePath) && !StringUtils.isEmpty(targetPath)) {
                            responseData = actionCopy(sourcePath, targetPath);
                        }
                        break;
                    case "move":
                        sourcePath = cleanPath(request.getParameter("old"));
                        targetPath = cleanPath(request.getParameter("new"));
                        if (!StringUtils.isEmpty(sourcePath) && !StringUtils.isEmpty(targetPath)) {
                            responseData = actionMove(sourcePath, targetPath);
                        }
                        break;
                    case "delete":
                        if (!StringUtils.isEmpty(pathParam)) {
                            responseData = actionDelete(pathParam);
                        }
                        break;
                    case "addfolder":
                        final String name = request.getParameter("name");
                        if (!StringUtils.isEmpty(pathParam) && !StringUtils.isEmpty(name)) {
                            responseData = actionAddFolder(pathParam, name);
                        }
                        break;
                    case "download":
                        if (!StringUtils.isEmpty(pathParam)) {
                            responseData = actionDownload(response, pathParam);
                        }
                        break;
                    case "getimage":
                        if (!StringUtils.isEmpty(pathParam)) {
                            Boolean thumbnail = Boolean.parseBoolean(request.getParameter("thumbnail"));
                            responseData = actionGetImage(response, pathParam, thumbnail);
                        }
                        break;
                    case "readfile":
                        if (!StringUtils.isEmpty(pathParam)) {
                            responseData = actionReadFile(request, response, pathParam);
                        }
                        break;
                    case "summarize":
                        responseData = actionSummarize();
                        break;
                }
            } else if (method.equals("POST")) {
                switch (mode) {
                    default:
                        throw new FileManagerException(ClientErrorMessage.MODE_ERROR);
                    case "upload":
                        if (!StringUtils.isEmpty(pathParam)) {
                            responseData = actionUpload(request, pathParam);
                        }
                        break;
                    case "savefile":
                        // final String contentParam = cleanPath(request.getParameter("content"));
                        String contentParam = request.getParameter("content");
                        if (!StringUtils.isEmpty(pathParam) && !StringUtils.isEmpty(contentParam)) {
                            responseData = actionSaveFile(pathParam, contentParam);
                        }
                        break;
                    case "extract":
                        sourcePath = cleanPath(request.getParameter("source"));
                        targetPath = cleanPath(request.getParameter("target"));
                        if (!StringUtils.isEmpty(sourcePath) && !StringUtils.isEmpty(targetPath)) {
                            responseData = actionExtract(sourcePath, targetPath);
                        }
                        break;
                }
            }

            if (responseData != null) {
                generateResponse(response, responseData);
            }

        } catch (FileManagerException e) {
            logger.info(e.getMessage(), e);
            generateErrorResponse(response, e.getMessage(), e.getArguments());
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            generateErrorResponse(response, "ERROR_SERVER", null);
        }

    }

    protected final boolean isAllowedFileExtension(String file) {
        String extension = FileUtils.getExtension(file).toLowerCase();

        boolean policyAllow = Boolean.parseBoolean(propertiesConfig.getProperty("extensions.policy.allow"));
        List<String> restrictions = Arrays.asList(propertiesConfig.getProperty("extensions.restrictions").split(","));

        if (policyAllow) {
            return restrictions.contains(extension);
        } else {
            return !restrictions.contains(extension);
        }
    }

    protected final boolean isAllowedImageExt(String ext) {
        return Arrays.asList(propertiesConfig.getProperty("images.extensions").split(",")).contains(ext.toLowerCase());
    }

    protected final boolean isAllowedPattern(String name, boolean isDir) throws FileManagerException {

        boolean policyAllow = Boolean.parseBoolean(propertiesConfig.getProperty("patterns.policy.allow"));
        try {
            if (isDir) {
                List<String> restrictionsFolder =
                    Arrays.asList(propertiesConfig.getProperty("patterns.restrictions.folder").split(","));
                boolean isMatch = false;
                for (String regex : restrictionsFolder) {
                    if (name.matches(regex)) {
                        isMatch = true;
                    }
                }

                return policyAllow == isMatch;

            } else {
                List<String> restrictionsFile =
                    Arrays.asList((propertiesConfig.getProperty("patterns.restrictions.file").split(",")));
                boolean isMatch = false;
                for (String regex : restrictionsFile) {
                    if (name.matches(regex)) {
                        isMatch = true;
                    }
                }

                return policyAllow == isMatch;
            }
        } catch (PatternSyntaxException e) {
            logger.error("Regex Dir Syntax Exception : " + propertiesConfig.getProperty("excluded_dirs_REGEXP"), e);
            throw new FileManagerException(ClientErrorMessage.ERROR_SERVER);
        }
    }

    protected boolean isMatchRestriction(File file) throws FileManagerException {
        return (file.isDirectory()) ? isAllowedPattern(file.getName(), true)
            : isAllowedFileExtension(file.getName()) && isAllowedPattern(file.getName(), false);
    }

    protected String normalizeName(String input) {

        boolean normalizeFilename = Boolean.parseBoolean(propertiesConfig.getProperty("normalizeFilename"));

        if (!normalizeFilename) {
            return input;
        }
        boolean charsLatinOnly = Boolean.parseBoolean(propertiesConfig.getProperty("charsLatinOnly"));

        String nowhitespace = WHITESPACE.matcher(input).replaceAll("_");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);

        return charsLatinOnly ? NONLATIN.matcher(normalized).replaceAll("") : normalized;
    }

    public void setPropertiesConfig(Properties propertiesConfig) {
        this.propertiesConfig = propertiesConfig;
    }
}