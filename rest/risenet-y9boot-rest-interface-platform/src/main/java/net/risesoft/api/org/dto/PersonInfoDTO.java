package net.risesoft.api.org.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;

/**
 * @author shidaobang
 * @date 2023/11/06
 * @since 9.6.3
 */
@Getter
@Setter
public class PersonInfoDTO implements Serializable {

    private static final long serialVersionUID = 7283067452731317208L;

    /** 人员信息 */
    private Person person;

    /** 岗位列表 */
    private List<Position> positionList;

}
