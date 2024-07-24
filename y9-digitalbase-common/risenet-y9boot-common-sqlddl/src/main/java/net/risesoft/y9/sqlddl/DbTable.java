package net.risesoft.y9.sqlddl;

import java.io.Serializable;

/**
 *
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
public class DbTable implements Serializable {
    private static final long serialVersionUID = 6672185772405591089L;

    private String catalog;
    private String schema;

    private String name;
    private String type;
    private String remarks;

    public String getCatalog() {
        return catalog;
    }

    public String getName() {
        return name;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getSchema() {
        return schema;
    }

    public String getType() {
        return type;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setType(String type) {
        this.type = type;
    }

}
