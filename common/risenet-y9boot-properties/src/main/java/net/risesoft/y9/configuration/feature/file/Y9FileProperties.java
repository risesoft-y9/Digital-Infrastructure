package net.risesoft.y9.configuration.feature.file;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.feature.file.ftp.Y9FtpProperties;
import net.risesoft.y9.configuration.feature.file.nfs.Y9NfsProperties;
import net.risesoft.y9.configuration.feature.file.rest.Y9RestFileProperties;
import net.risesoft.y9.configuration.feature.file.samba.Y9SambaProperties;
import net.risesoft.y9.configuration.feature.file.wps.WPSProperties;

/**
 * 文件存储配置
 *
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
public class Y9FileProperties {

    /**
     * 是否对文件名 base64 编码
     */
    private boolean base64FileName = false;

    /**
     * 是否用对称加密算法 AES 加密文件内容
     */
    private boolean encryptionFileContent = false;

    /**
     * 对于不能动态扩容的存储空间 可以通过分目录挂新盘扩展存储空间
     */
    private String prefix = "";

    /**
     * 私钥
     */
    private String privateKey =
        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ3HwsCHIKz3CErQS7+NFqLAW5SGHw+J444ozfuZ/Sm6pLUzIDksJ7//mtmBDKydfx+3dXbqCjd/QGYbV+XCk4FmfGJHy4WpaUwOc5wh2UoCBE9SEdXOpmvFSV50HcWxUncsc6ufr/Bpy5Ktks3RsZ0c73lEE6FOFezcTlbtoDMhAgMBAAECgYAgK0Rn4KUm3s8QAdwP2AJPeIyzgYz/rAt7RpKIw+K8CVPfpebiAUCxgrndstQUtZ/fpZYLgrhGjGli6BxJuhw8qVpYu01APMbLGj3JhrAWT0zPMQw+JmmIyHKl8q43Dy8/dvhZ+jZdf6WRJxldyMLLJszUqVPsU/eAxEiKALfRgQJBAOGW3YHOE95a5dO1yKA/1Nc+wb2gVX9tKzCh5ZrLDu6a3GmW7Lk9gtuMtOi0r1AlwgvgJH6aaAKFTnE76x14FhkCQQCzDMrmwH1w86fsoha0zJez9zQIyVcunxPdRudySK3/VG0VGG0obGEaKT1lo3AewJ7qb6I1T7uAI9Iwf2QWUdZJAkBdingpBfGZJunbwqoBQNaZti0R2zT4lKTvEoKpj/+OEurIYcug+A+VyB+PyrRTMITo9bVMRexQ90PSkjzoyE2pAkAOK84HU2baQL6isPWBG8xJ9x/MLjtTOk31Ln51AiGbWtBDYiqJj4Jj8q2kVLo0BOTPA0TgWU4qxysEoaCHT7TZAkEAqmnxx2euNUc++xsPOHTJHtKts+K8IgTxldAJCTDVO3eltU69bKHd/tUBen/oyUJ9OWGp+WTFxyh8vQ4rF5kZkw==";

    /**
     * 公钥
     */
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdx8LAhyCs9whK0Eu/jRaiwFuUhh8PieOOKM37mf0puqS1MyA5LCe//5rZgQysnX8ft3V26go3f0BmG1flwpOBZnxiR8uFqWlMDnOcIdlKAgRPUhHVzqZrxUledB3FsVJ3LHOrn6/wacuSrZLN0bGdHO95RBOhThXs3E5W7aAzIQIDAQAB";

    /**
     * ftp
     */
    @NestedConfigurationProperty
    private Y9FtpProperties ftp = new Y9FtpProperties();

    /**
     * 文件属性对象
     */
    @NestedConfigurationProperty
    private Y9RestFileProperties rest = new Y9RestFileProperties();

    /**
     * samba属性对象
     */
    @NestedConfigurationProperty
    private Y9SambaProperties samba = new Y9SambaProperties();

    /**
     * nfs属性对象
     */
    @NestedConfigurationProperty
    private Y9NfsProperties nfs = new Y9NfsProperties();

    /**
     * wps属性对象
     */
    @NestedConfigurationProperty
    private WPSProperties wps = new WPSProperties();

}
