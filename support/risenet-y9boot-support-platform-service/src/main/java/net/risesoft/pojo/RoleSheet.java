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
public class RoleSheet implements Serializable {

    private static final long serialVersionUID = 5180112495471953564L;

    private Integer num;
    private List<?> personList;
    private String roleDn;
    private String roleName;

}
