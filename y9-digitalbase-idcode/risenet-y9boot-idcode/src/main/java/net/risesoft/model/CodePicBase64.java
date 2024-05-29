package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CodePicBase64 extends Result{

    @JsonProperty("code_pic_base64str")
    private String str;
}
