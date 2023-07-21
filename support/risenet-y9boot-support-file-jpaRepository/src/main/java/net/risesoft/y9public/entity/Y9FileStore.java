package net.risesoft.y9public.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import net.risesoft.y9.util.Y9FileUtil;
import net.risesoft.y9public.support.FileNameConverter;

@Entity
@Table(name = "Y9_COMMON_FILE_STORE")
@org.hibernate.annotations.Table(comment = "文件仓库表", appliesTo = "Y9_COMMON_FILE_STORE")
// @EntityListeners({ Y9FileStoreListener.class })
public class Y9FileStore implements Serializable {
    
    private static final long serialVersionUID = 5215025303846508704L;
    
    @Id
    @Column(name = "STOREID", length = 38)
    @Comment("主键")
    private String id;

    @Column(name = "STORETYPE")
    @Comment("仓库类型")
    private Integer storeType = 1; // 1=ftp、2=wps云存储、3=rest、4=nfs、5=samba

    @Column(name = "FULLPATH", length = 300)
    @Comment("绝对路径")
    private String fullPath; // 在仓库中的绝对路径。

    @Convert(converter = FileNameConverter.class)
    @Column(name = "FILENAME", length = 600)
    @Comment("上传的文件名称")
    private String fileName; // 包含扩展名称，用户上传的文件名称，有可能包含特殊字符，也很长，实际存储的名称是realFileName

    @Column(name = "REALFILENAME", length = 100, nullable = true)
    @Comment("存放的文件名称")
    private String realFileName; // 包含扩展名称，操作系统中实际存储的文件名称，格式为{id}.ext 这样可以解决文件名过长及乱码的问题

    @Column(name = "FILESIZE", nullable = true)
    @ColumnDefault("0")
    @Comment("文件长度")
    private Long fileSize;

    @Column(name = "FILESHA", length = 200, nullable = true)
    @Comment("文件SHA值")
    private String fileSha;

    @Column(name = "FILEENVELOPE", length = 200, nullable = true)
    @Comment("文件数字信封: 即AES的随机密钥，然后进行RSA加密后的结果")
    private String fileEnvelope;

    @Column(name = "FILEEXT", length = 50, nullable = true)
    @Comment("文件扩展名称")
    private String fileExt; // 冗余字段，只保存文件扩展名

    @Column(name = "FILEURL", length = 500)
    @Comment("完整的文件地址")
    private String url; // /s/{id}.ext

    @Column(name = "SYSTEMNAME", length = 50)
    @Comment("系统名称")
    private String systemName;

    @Column(name = "PREFIX", length = 50)
    @Comment("根目录前缀") // 格式为：/disk01的形式，以字符/开头，尾部不包含字符/
    private String prefix;

    @Column(name = "TENANTID", length = 38)
    @Comment("租户Id")
    private String tenantId;

    @Column(name = "UPLOADER", length = 100)
    @Comment("上传人")
    private String uploader;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPLOADTIME")
    @Comment("上传时间")
    private Date uploadTime;

    public Y9FileStore() {}
    
    public static String buildFullPath(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            if (StringUtils.hasText(path)) {
                for (int i = 0; i < 10; i++) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    } else {
                        break;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    if (path.endsWith("/")) {
                        path = path.substring(0, path.length() - 1);
                    } else {
                        break;
                    }
                }

                sb.append("/").append(path);
            }
        }
        return sb.toString();
    }

    public String getFileExt() {
        return fileExt;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSha() {
        return fileSha;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getDisplayFileSize() {
        String displayFileSize = "0B";
        if (fileSize != null) {
            Y9FileUtil.getDisplayFileSize(fileSize);
        }
        return displayFileSize;
    }

    public String getFullPath() {
        return fullPath;
    }

    /**
     * @return the fileEnvelope
     */
    public String getFileEnvelope() {
        return fileEnvelope;
    }

    /**
     * @param fileEnvelope the fileEnvelope to set
     */
    public void setFileEnvelope(String fileEnvelope) {
        this.fileEnvelope = fileEnvelope;
    }

    public String getFullPathAndFileName() {
        String fullName = this.fullPath;
        if (!fullName.endsWith("/")) {
            fullName = fullName + "/";
        }
        fullName += this.fileName;
        return fullName;
    }

    public String getFullPathAndRealFileName() {
        String fullName = this.fullPath;
        if (!fullName.endsWith("/")) {
            fullName = fullName + "/";
        }
        fullName += getRealFileName();
        return fullName;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public Integer getStoreType() {
        return storeType;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getUploader() {
        return uploader;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public String getUrl() {
        return url;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSha(String fileSha) {
        this.fileSha = fileSha;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public void setStoreType(Integer storeType) {
        this.storeType = storeType;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileExt == null) ? 0 : fileExt.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((fileSha == null) ? 0 : fileSha.hashCode());
        result = prime * result + ((fileSize == null) ? 0 : fileSize.hashCode());
        result = prime * result + ((fullPath == null) ? 0 : fullPath.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        result = prime * result + ((realFileName == null) ? 0 : realFileName.hashCode());
        result = prime * result + ((storeType == null) ? 0 : storeType.hashCode());
        result = prime * result + ((systemName == null) ? 0 : systemName.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        result = prime * result + ((uploadTime == null) ? 0 : uploadTime.hashCode());
        result = prime * result + ((uploader == null) ? 0 : uploader.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Y9FileStore other = (Y9FileStore)obj;
        if (fileExt == null) {
            if (other.fileExt != null) {
                return false;
            }
        } else if (!fileExt.equals(other.fileExt)) {
            return false;
        }
        if (fileName == null) {
            if (other.fileName != null) {
                return false;
            }
        } else if (!fileName.equals(other.fileName)) {
            return false;
        }
        if (fileSha == null) {
            if (other.fileSha != null) {
                return false;
            }
        } else if (!fileSha.equals(other.fileSha)) {
            return false;
        }
        if (fileSize == null) {
            if (other.fileSize != null) {
                return false;
            }
        } else if (!fileSize.equals(other.fileSize)) {
            return false;
        }
        if (fullPath == null) {
            if (other.fullPath != null) {
                return false;
            }
        } else if (!fullPath.equals(other.fullPath)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (prefix == null) {
            if (other.prefix != null) {
                return false;
            }
        } else if (!prefix.equals(other.prefix)) {
            return false;
        }
        if (realFileName == null) {
            if (other.realFileName != null) {
                return false;
            }
        } else if (!realFileName.equals(other.realFileName)) {
            return false;
        }
        if (storeType == null) {
            if (other.storeType != null) {
                return false;
            }
        } else if (!storeType.equals(other.storeType)) {
            return false;
        }
        if (systemName == null) {
            if (other.systemName != null) {
                return false;
            }
        } else if (!systemName.equals(other.systemName)) {
            return false;
        }
        if (tenantId == null) {
            if (other.tenantId != null) {
                return false;
            }
        } else if (!tenantId.equals(other.tenantId)) {
            return false;
        }
        if (uploadTime == null) {
            if (other.uploadTime != null) {
                return false;
            }
        } else if (!uploadTime.equals(other.uploadTime)) {
            return false;
        }
        if (uploader == null) {
            if (other.uploader != null) {
                return false;
            }
        } else if (!uploader.equals(other.uploader)) {
            return false;
        }
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Y9FileStore [id=" + id + ", storeType=" + storeType + ", fullPath=" + fullPath + ", fileName=" + fileName + ", realFileName=" + realFileName + ", fileSize=" + fileSize + ", fileSha=" + fileSha + ", fileExt=" + fileExt + ", url=" + url + ", systemName=" + systemName + ", prefix="
            + prefix + ", tenantId=" + tenantId + ", uploader=" + uploader + ", uploadTime=" + uploadTime + "]";
    }

}
