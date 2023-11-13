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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import net.risesoft.enums.FileStoreTypeEnum;
import net.risesoft.persistence.EnumConverter;
import net.risesoft.y9.util.Y9FileUtil;
import net.risesoft.y9public.support.FileNameConverter;

@Entity
@Table(name = "Y9_COMMON_FILE_STORE")
@org.hibernate.annotations.Table(comment = "文件仓库表", appliesTo = "Y9_COMMON_FILE_STORE")
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Y9FileStore implements Serializable {

    private static final long serialVersionUID = 5215025303846508704L;

    @Id
    @Column(name = "STOREID", length = 38)
    @Comment("主键")
    private String id;

    @Column(name = "STORETYPE")
    @Comment("仓库类型")
    @Convert(converter = EnumConverter.FileStoreTypeEnumConverter.class)
    private FileStoreTypeEnum storeType = FileStoreTypeEnum.FTP;

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

    public String getDisplayFileSize() {
        String displayFileSize = "0B";
        if (fileSize != null) {
            Y9FileUtil.getDisplayFileSize(fileSize);
        }
        return displayFileSize;
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

}
