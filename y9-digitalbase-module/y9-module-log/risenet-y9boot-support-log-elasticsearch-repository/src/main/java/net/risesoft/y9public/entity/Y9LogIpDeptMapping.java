package net.risesoft.y9public.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Document(indexName = "ipdeptmapping")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Y9LogIpDeptMapping implements Serializable {
    private static final long serialVersionUID = -3758903946162468650L;

    @Id
    private String id;

    /** clientIp的ABC段 */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String clientIpSection;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String operator;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String deptName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String saveTime;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String updateTime;

    @Field(type = FieldType.Integer, index = true, store = true)
    private Integer tabIndex;

    @Field(type = FieldType.Integer, index = true, store = true)
    private Integer status;

    /** 租户ID */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String tenantId;

}
