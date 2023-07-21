package net.risesoft.log.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "log_mapping_index")
@Data
@NoArgsConstructor
public class Y9logMapping implements Serializable {
    private static final long serialVersionUID = -290275690477972055L;

    @Id
    private String id;

    /**
     * 模块名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String modularName;

    /**
     * 模块中文名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String modularCnName;
}
