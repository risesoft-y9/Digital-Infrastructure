package net.risesoft.model.platform.resource;

import java.util.List;

import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 操作导出 JSON 模型
 *
 * @author shidaobang
 * @since 9.6.10
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class OperationJsonModel extends Operation {

    private static final long serialVersionUID = -7493930537365048851L;

    @Valid
    private List<MenuJsonModel> subMenuList;

    @Valid
    private List<OperationJsonModel> subOperationList;

}
