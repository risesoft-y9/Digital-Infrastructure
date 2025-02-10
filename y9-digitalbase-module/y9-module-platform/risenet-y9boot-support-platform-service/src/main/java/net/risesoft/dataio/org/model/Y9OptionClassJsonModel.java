package net.risesoft.dataio.org.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.Y9OptionClass;

/**
 * Y9OptionClass 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2025/02/08
 */
@Getter
@Setter
public class Y9OptionClassJsonModel extends Y9OptionClass {

    private static final long serialVersionUID = 1906765990626889727L;

    private List<Y9OptionValueJsonModel> optionValueList = new ArrayList<>();

}
