package net.risesoft.pojo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
public class ObjectSheet implements Serializable {

    private static final long serialVersionUID = -2404495986373823513L;
    private String dn;
    private String name;
    private int num;
    private List<PersonSheet> personList;

}
