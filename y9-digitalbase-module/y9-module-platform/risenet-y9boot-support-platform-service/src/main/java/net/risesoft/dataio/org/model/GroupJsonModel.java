package net.risesoft.dataio.org.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.org.Y9Group;

/**
 * Y9Group 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2025/02/08
 */
@Getter
@Setter
public class GroupJsonModel extends Y9Group {

    private static final long serialVersionUID = -5375281958304678636L;

    private List<PersonsToGroupsJsonModel> personsToGroupsList = new ArrayList<>();

}
