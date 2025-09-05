package net.risesoft.y9public.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(indexName = "appclickindex")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Y9ClickedApp implements Serializable {

    private static final long serialVersionUID = 144334145599572308L;

    @Id
    private String id;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String personId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String tenantId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String appId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String appName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private Date saveDate;
}
