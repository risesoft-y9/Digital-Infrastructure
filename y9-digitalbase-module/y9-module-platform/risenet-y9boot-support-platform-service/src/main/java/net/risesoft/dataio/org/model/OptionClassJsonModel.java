package net.risesoft.dataio.org.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.model.platform.dictionary.OptionClass;

/**
 * Y9OptionClass 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2025/02/08
 */
@Getter
@Setter
public class OptionClassJsonModel extends OptionClass {

    private static final long serialVersionUID = 1906765990626889727L;

    private List<OptionValueJsonModel> optionValueList = new ArrayList<>();

}
