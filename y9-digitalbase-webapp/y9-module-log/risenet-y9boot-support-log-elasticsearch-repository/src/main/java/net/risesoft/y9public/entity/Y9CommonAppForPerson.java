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
@Document(indexName = "common_app_for_person")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Y9CommonAppForPerson implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6201670055765212286L;

    @Id
    private String id;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String personId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String tenantId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String appIds;
}
