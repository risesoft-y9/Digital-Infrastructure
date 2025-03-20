package net.risesoft.api.platform.org.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * 职位
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2025/3/19
 * @since 9.6.8
 */
@Getter
@Setter
public class CreateJobDTO {

    /** 数据代码 */
    private String code;

    /** 名称 */
    @NotBlank
    private String name;
}