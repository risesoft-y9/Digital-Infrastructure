package net.risesoft.y9.configuration.app.y9digitalbase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9DigitalBaseProperties {

    private String systemName = "risedigitalBase";

    /** 人员管理的菜单ID */
    private String menuId = "54f055c4368c485ab762dfd88c5d70e5";

    /** 下载临时存放目录 */
    private String downloadTempDir = "//home//y9admin";

    private String saveSoaEable = "false";

    private String orclNewTableSpace = "/u01/app/oracle/oradata/orcl/";

    private String y9IsvGuid = "993f08e4a9624c7d82b0bd0766f45f10";

    private String headMenuGuid = "504e0a3948c548c7a36bfc2bbaa43adf";
    
}
