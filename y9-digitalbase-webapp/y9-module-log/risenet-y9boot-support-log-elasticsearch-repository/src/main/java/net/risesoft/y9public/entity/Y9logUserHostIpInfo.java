package net.risesoft.y9public.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Document(indexName = "userhostipinfo")
public class Y9logUserHostIpInfo implements Serializable {
    private static final long serialVersionUID = -6096173983030412296L;

    @Id
    private String id;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userHostIp;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String clientIpSection;

}
