package net.risesoft.y9public.ftp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FtpClientHelper {

    private FtpClientPool ftpClientPool;

    /**
     * 删除文件 单个 ，不可递归
     *
     * @param pathname 带路径的文件名称
     * @return boolean 删除状态
     * @throws Exception error occurs.
     */
    public boolean deleteFile(String pathname) throws Exception {
        FTPClient client = null;
        try {
            client = ftpClientPool.borrowObject();
            // new String(pathname.getBytes(), "ISO-8859-1");
            String encodedName = pathname;
            return client.deleteFile(encodedName);
        } catch (Exception e) {
            LOGGER.error("删除文件失败", e);
            throw e;
        } finally {
            ftpClientPool.returnObject(client);
        }
    }

    /**
     * Creates a nested directory structure on a FTP server
     * 
     * @param client object instance from the pool
     * @param dirPath Path of the directory, i.e /projects/java/ftp/demo
     * @return boolean true if the directory was created successfully, false otherwise
     * @throws Exception – error occurs.
     */
    public boolean makeDirectories(FTPClient client, String dirPath) throws Exception {
        int errCount = 0;
        String[] pathElements = dirPath.split("/");
        if (pathElements != null && pathElements.length > 0) {
            client.changeWorkingDirectory("/");
            for (String singleDir : pathElements) {
                if (StringUtils.isBlank(singleDir)) {
                    continue;
                }

                // 有的ftp server的目录不支持中文，要编码。
                // new String(singleDir.getBytes(), "ISO-8859-1");
                String encodedDir = singleDir;
                // encodedDir目录可能已经存在了。
                boolean created = client.makeDirectory(encodedDir);
                // 切换到这个目录
                boolean changed = client.changeWorkingDirectory(encodedDir);
                if (!changed) {
                    // 切换失败，错误数加一
                    errCount += 1;
                }
            }
        }
        return errCount == 0;
    }

    /**
     * 创建目录 单个不可递归
     *
     * @param client object instance from the pool
     * @param pathname 目录名称
     * @return boolean true if the directory was created successfully, false otherwise
     * @throws Exception error occurs.
     */
    public boolean makeDirectory(FTPClient client, String pathname) throws Exception {
        // new String(pathname.getBytes(), "ISO-8859-1");
        String encodedName = pathname;
        return client.makeDirectory(encodedName);
    }

    /**
     * 删除目录，单个不可递归
     *
     * @param pathname 目录名称
     * @return boolean true if the File is remove successfully, false otherwise
     * @throws IOException error occurs.
     */
    public boolean removeDirectory(String pathname) throws Exception {
        FTPClient client = null;
        try {
            client = ftpClientPool.borrowObject();
            // new String(pathname.getBytes(), "ISO-8859-1");
            String encodedName = pathname;
            return client.removeDirectory(encodedName);
        } catch (Exception e) {
            LOGGER.error("删除目录失败", e);
            throw e;
        } finally {
            ftpClientPool.returnObject(client);
        }
    }

    /**
     * 下载remote文件字节
     *
     * @param remote 远程文件
     * @return 字节数据
     * @throws Exception error occurs.
     */
    public byte[] retrieveFileBytes(String remote) throws Exception {
        long start = System.currentTimeMillis();
        FTPClient client = ftpClientPool.borrowObject();
        try (InputStream in = client.retrieveFileStream(remote)) {
            long end = System.currentTimeMillis();
            LOGGER.info("ftp下载耗时(毫秒):" + (end - start));

            return ByteUtil.inputStreamToByteArray(in);
        } catch (Exception e) {
            LOGGER.error("获取ftp下载流异常", e);
        } finally {
            client.completePendingCommand();
            ftpClientPool.returnObject(client);
        }
        return new byte[0];
    }

    public boolean retrieveFileStream(String remote, OutputStream local) throws Exception {
        FTPClient client = ftpClientPool.borrowObject();
        boolean ret = false;
        try {
            long start = System.currentTimeMillis();
            ret = client.retrieveFile(remote, local);

            long end = System.currentTimeMillis();
            LOGGER.info("ftp下载耗时(毫秒):" + (end - start));

            return ret;

        } catch (Exception e) {
            LOGGER.error("获取ftp下载流异常", e);
        } finally {
            ftpClientPool.returnObject(client);
        }
        return ret;
    }

    public void setFtpClientPool(FtpClientPool ftpClientPool) {
        this.ftpClientPool = ftpClientPool;
    }

    public boolean storeFile(String fullPath, String realFileName, byte[] bytes) throws Exception {
        FTPClient client = ftpClientPool.borrowObject();
        try {
            makeDirectories(client, fullPath);
            InputStream targetStream = new ByteArrayInputStream(bytes);
            return client.storeFile(realFileName, targetStream);
        } catch (Exception e) {
            LOGGER.error("上传文件失败", e);
            throw e;
        } finally {
            ftpClientPool.returnObject(client);
        }
    }

    /**
     * 上传文件
     *
     * @param fullPath 路径
     * @param realFileName 真实文件名称
     * @param local 文件流
     * @return boolean true if the File was upload successfully, false otherwise
     * @throws Exception error occurs.
     */
    public boolean storeFile(String fullPath, String realFileName, InputStream local) throws Exception {
        FTPClient client = ftpClientPool.borrowObject();
        try {
            makeDirectories(client, fullPath);
            return client.storeFile(realFileName, local);
        } catch (Exception e) {
            LOGGER.error("上传文件失败", e);
            throw e;
        } finally {
            ftpClientPool.returnObject(client);
        }
    }

}
