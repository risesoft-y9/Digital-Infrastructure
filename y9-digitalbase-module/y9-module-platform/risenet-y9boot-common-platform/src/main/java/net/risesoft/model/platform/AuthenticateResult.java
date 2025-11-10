package net.risesoft.model.platform;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.model.platform.org.Person;

/**
 * 认证结果
 * 
 * @author shidaobang
 * @date 2023/10/31
 * @since 9.6.3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateResult implements Serializable {

    private static final long serialVersionUID = 798527854696823939L;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 人员
     */
    private Person person;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 委办局名称
     */
    private String bureauName;

}
