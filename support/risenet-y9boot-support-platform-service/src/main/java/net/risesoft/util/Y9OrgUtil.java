package net.risesoft.util;

import java.util.Objects;

import net.risesoft.entity.Y9OrgBase;

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
        return b.getGuidPath().contains(a.getGuidPath()) && !Objects.equals(a.getGuidPath(), b.getGuidPath());
    }

    /**
     * 组织节点a是否为组织节点b的子孙节点
     *
     * @param a 组织节点a
     * @param b 组织节点b
     * @return boolean
     */
    public static boolean isSameOf(Y9OrgBase a, Y9OrgBase b) {
        return Objects.equals(a.getId(), b.getId());
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
}
