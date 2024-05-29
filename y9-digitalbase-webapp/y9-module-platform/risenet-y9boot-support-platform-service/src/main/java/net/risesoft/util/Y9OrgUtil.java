package net.risesoft.util;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.enums.platform.OrgTypeEnum;

/**
 * 组织工具类
 *
 * @author shidaobang
 * @date 2022/12/02
 */
public class Y9OrgUtil {

    /**
     * 判断组织节点是否有移动
     * 
     * @param originOrgBase 原始组织节点对象
     * @param updateOrgBase 更新后组织节点对象
     * @return
     */
    public static boolean isMoved(Y9OrgBase originOrgBase, Y9OrgBase updateOrgBase) {
        return !Objects.equals(originOrgBase.getParentId(), updateOrgBase.getParentId());
    }

    /**
     * 判断组织节点及其任一祖先节点是否有改变
     *
     * @param originOrgBase 原始组织节点对象
     * @param updateOrgBase 更新后组织节点对象
     * @return
     */
    public static boolean isAncestorChanged(Y9OrgBase originOrgBase, Y9OrgBase updateOrgBase) {
        return !Objects.equals(originOrgBase.getDn(), updateOrgBase.getDn())
            || !Objects.equals(originOrgBase.getGuidPath(), updateOrgBase.getGuidPath());
    }

    /**
     * 判断组织节点是否有改名
     *
     * @param originOrgBase 原始组织节点对象
     * @param updateOrgBase 更新后组织节点对象
     * @return
     */
    public static boolean isRenamed(Y9OrgBase originOrgBase, Y9OrgBase updateOrgBase) {
        return !Objects.equals(originOrgBase.getName(), updateOrgBase.getName());
    }

    /**
     * 组织节点a是否为组织节点b的祖先节点
     *
     * @param a 组织节点a
     * @param b 组织节点b
     * @return boolean
     */
    public static boolean isAncestorOf(Y9OrgBase a, Y9OrgBase b) {
        return isDescendantOf(b, a);
    }

    /**
     * 组织节点a和组织节点b是否为相同节点
     *
     * @param a 组织节点a
     * @param b 组织节点b
     * @return boolean
     */
    public static boolean isSameOf(Y9OrgBase a, Y9OrgBase b) {
        return Objects.equals(a, b);
    }

    /**
     * 组织节点a是否为组织节点b的子孙节点
     *
     * @param a 组织节点a
     * @param b 组织节点b
     * @return boolean
     */
    public static boolean isDescendantOf(Y9OrgBase a, Y9OrgBase b) {
        return a.getGuidPath().contains(b.getGuidPath()) && !Objects.equals(a.getGuidPath(), b.getGuidPath());
    }

    /**
     * 构建 dn
     *
     * @param orgTypeEnum 组织类型列举
     * @param name 名称
     * @param parentDn 父dn
     * @return {@code String }
     */
    public static String buildDn(OrgTypeEnum orgTypeEnum, String name, String parentDn) {
        if (StringUtils.isEmpty(parentDn)) {
            return OrgLevelConsts.getOrgLevel(orgTypeEnum) + name;
        }
        return OrgLevelConsts.getOrgLevel(orgTypeEnum) + name + OrgLevelConsts.SEPARATOR + parentDn;
    }

    /**
     * 构建 id 路径
     *
     * @param parentGuidPath 父 id 路径
     * @param id ID
     * @return {@code String }
     */
    public static String buildGuidPath(String parentGuidPath, String id) {
        if (StringUtils.isEmpty(parentGuidPath)) {
            return id;
        }
        return parentGuidPath + OrgLevelConsts.SEPARATOR + id;
    }
}
