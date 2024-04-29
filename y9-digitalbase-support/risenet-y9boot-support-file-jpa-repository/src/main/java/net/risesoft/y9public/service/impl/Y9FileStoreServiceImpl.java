package net.risesoft.y9public.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.crypto.AesUtil;
import net.risesoft.y9.util.crypto.RsaUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.repository.Y9FileStoreRepository;
import net.risesoft.y9public.service.StoreService;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 *
 */
@Service(value = "y9FileStoreService")
@Transactional(rollbackFor = Exception.class, transactionManager = "rsPublicTransactionManager")
@Slf4j
@RequiredArgsConstructor
public class Y9FileStoreServiceImpl implements Y9FileStoreService {

    private final Y9FileStoreRepository y9FileStoreRepository;
    private final Y9Properties y9Config;
    private final StoreService storeService;
    private final ThreadPoolTaskExecutor taskExecutor;

    private boolean encryptionFileContent = false;
    private String privateKey = "";
    private String publicKey = "";
    private String prefix = "";

    private String buildFullPath(String prefix, String customPath) {
        LocalDate localDate = LocalDate.now();
        String datePath = Y9FileStore.buildPath(String.valueOf(localDate.getYear()),
            String.valueOf(localDate.getMonthValue()), String.valueOf(localDate.getDayOfMonth()));
        if (StringUtils.isNotBlank(prefix)) {
            return Y9FileStore.buildPath(prefix, datePath, customPath);
        } else {
            return Y9FileStore.buildPath(datePath, customPath);
        }
    }

    private boolean decryptionRequired(Y9FileStore y9FileStore) {
        return encryptionFileContent && StringUtils.isNotBlank(y9FileStore.getFileEnvelope());
    }

    @Override
    public boolean deleteFile(String id) {
        Y9FileStore y9FileStore = this.getById(id);
        if (null != y9FileStore) {
            try {
                storeService.deleteFile(y9FileStore.getFullPath(), y9FileStore.getRealFileName());
                y9FileStoreRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    public byte[] downloadFileToBytes(String id) throws Exception {
        Y9FileStore y9FileStore = this.getById(id);
        byte[] bytes = storeService.retrieveFileBytes(y9FileStore.getFullPath(), y9FileStore.getRealFileName());
        if (decryptionRequired(y9FileStore)) {
            try {
                String key = RsaUtil.decryptByPubKey(y9FileStore.getFileEnvelope(), this.publicKey);
                bytes = AesUtil.decryptByte(bytes, key);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        return bytes;
    }

    @Override
    public void downloadFileToOutputStream(String id, OutputStream outputStream) throws Exception {
        Y9FileStore y9FileStore = this.getById(id);
        if (decryptionRequired(y9FileStore)) {
            try {
                String key = RsaUtil.decryptByPubKey(y9FileStore.getFileEnvelope(), this.publicKey);
                storeService.retrieveFileStream(y9FileStore.getFullPath(), y9FileStore.getRealFileName(),
                    AesUtil.decryptStream(key, outputStream));
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        } else {
            storeService.retrieveFileStream(y9FileStore.getFullPath(), y9FileStore.getRealFileName(), outputStream);
        }
    }

    @Override
    public String downloadFileToString(String id) throws Exception {
        byte[] bytes = this.downloadFileToBytes(id);
        return new String(bytes, "UTF-8");
    }

    @Override
    public Y9FileStore getById(String y9FileStoreId) {
        return y9FileStoreRepository.findById(y9FileStoreId).orElse(null);
    }

    @PostConstruct
    public void init() {
        this.encryptionFileContent = this.y9Config.getFeature().getFile().isEncryptionFileContent();
        this.privateKey = this.y9Config.getFeature().getFile().getPrivateKey();
        this.publicKey = this.y9Config.getFeature().getFile().getPublicKey();
        this.prefix = this.y9Config.getFeature().getFile().getPrefix();
    }

    private Y9FileStore saveY9FileStore(String customPath, String fileName, String fileEnvelope, long fileSize) {
        Y9FileStore y9FileStore = new Y9FileStore();

        String y9FileStoreId = Y9IdGenerator.genId(IdType.SNOWFLAKE);

        y9FileStore.setId(y9FileStoreId);
        y9FileStore.setPrefix(prefix);
        y9FileStore.setSystemName(Y9Context.getSystemName());
        y9FileStore.setTenantId(Y9LoginUserHolder.getTenantId());
        if (Y9LoginUserHolder.getUserInfo() != null) {
            y9FileStore.setUploader(Y9LoginUserHolder.getUserInfo().getName());
        }
        y9FileStore.setUploadTime(new Date());
        y9FileStore.setFullPath(buildFullPath(prefix, customPath));
        y9FileStore.setFileName(fileName);
        String fileExt = FilenameUtils.getExtension(fileName);
        String realFileName = y9FileStoreId + "." + fileExt;
        String url = "/files/" + realFileName;

        y9FileStore.setStoreType(storeService.getStoreType());
        y9FileStore.setFileExt(fileExt);
        y9FileStore.setRealFileName(realFileName);
        y9FileStore.setFileSize(fileSize);
        y9FileStore.setUrl(url);
        y9FileStore.setFileEnvelope(fileEnvelope);
        return y9FileStoreRepository.save(y9FileStore);
    }

    @Override
    public Y9FileStore uploadFile(byte[] bytes, String customPath, String fileName) throws Exception {
        return this.uploadFile(new ByteArrayInputStream(bytes), customPath, fileName);
    }

    @Override
    public Y9FileStore uploadFile(File file, String customPath, String fileName) throws Exception {
        return this.uploadFile(new FileInputStream(file), customPath, fileName);
    }

    @Override
    public Y9FileStore uploadFile(InputStream inputStream, String customPath, String fileName) throws Exception {
        int fileSize = inputStream.available();
        String fileEnvelope = null;
        if (encryptionFileContent) {
            try {
                // 获得随机AES密钥
                String aesKey = AesUtil.getSecretKey();
                // 对随机AES密钥进行RSA加密
                fileEnvelope = RsaUtil.encryptByPriKey(aesKey, this.privateKey);

                inputStream = AesUtil.encryptStream(aesKey, inputStream);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        Y9FileStore y9FileStore = saveY9FileStore(customPath, fileName, fileEnvelope, fileSize);

        storeService.storeFile(y9FileStore.getFullPath(), y9FileStore.getRealFileName(), inputStream);

        return y9FileStore;
    }

    @Override
    public Y9FileStore uploadFile(MultipartFile multipartFile, String customPath, String fileName) throws Exception {
        return this.uploadFile(multipartFile.getInputStream(), customPath, fileName);
    }

    @Override
    public Y9FileStore uploadFileAsync(byte[] bytes, String customPath, String fileName) throws Exception {
        return this.uploadFileAsync(new ByteArrayInputStream(bytes), customPath, fileName);
    }

    @Override
    public Y9FileStore uploadFileAsync(File file, String customPath, String fileName) throws Exception {
        return this.uploadFileAsync(new FileInputStream(file), customPath, fileName);
    }

    @Override
    public Y9FileStore uploadFileAsync(InputStream inputStream, String customPath, String fileName) throws Exception {
        int fileSize = inputStream.available();
        String fileEnvelope = null;
        if (encryptionFileContent) {
            try {
                // 获得随机AES密钥
                String aesKey = AesUtil.getSecretKey();
                // 对随机AES密钥进行RSA加密
                fileEnvelope = RsaUtil.encryptByPriKey(aesKey, this.privateKey);

                inputStream = AesUtil.encryptStream(aesKey, inputStream);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        Y9FileStore y9FileStore = saveY9FileStore(customPath, fileName, fileEnvelope, fileSize);

        InputStream finalInputStream = inputStream;
        Future<Boolean> submit = taskExecutor.submit(() -> {
            try {
                storeService.storeFile(y9FileStore.getFullPath(), y9FileStore.getRealFileName(), finalInputStream);
                return Boolean.TRUE;
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }

            return Boolean.FALSE;
        });
        return y9FileStoreRepository.save(y9FileStore);
    }

    @Override
    public Y9FileStore uploadFileAsync(MultipartFile multipartFile, String customPath, String fileName)
        throws Exception {
        return this.uploadFileAsync(multipartFile.getInputStream(), customPath, fileName);
    }

    @Override
    public Y9FileStore uploadFileReplace(byte[] bytes, String y9FileStoreId) throws Exception {
        return this.uploadFileReplace(new ByteArrayInputStream(bytes), y9FileStoreId);
    }

    @Override
    public Y9FileStore uploadFileReplace(File file, String y9FileStoreId) throws Exception {
        return this.uploadFileReplace(new FileInputStream(file), y9FileStoreId);
    }

    @Override
    public Y9FileStore uploadFileReplace(InputStream inputStream, String y9FileStoreId) throws Exception {
        Y9FileStore y9FileStore = this.getById(y9FileStoreId);
        this.deleteFile(y9FileStoreId);
        return this.uploadFile(inputStream, y9FileStore.getFullPath(), y9FileStore.getFileName());
    }

    @Override
    public Y9FileStore uploadFileReplace(MultipartFile multipartFile, String y9FileStoreId) throws Exception {
        return this.uploadFileReplace(multipartFile.getInputStream(), y9FileStoreId);
    }

}
