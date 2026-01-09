package net.risesoft.util;

import java.util.Objects;

import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * 资源工具类
 *
 * @author shidaobang
 * @date 2022/12/30
 */
public class Y9ResourceUtil {

    /**
     * 继承属性是否改变
     *
     * @param originResourceBase 原始资源对象
     * @param updateResourceBase 更新后的资源对象
     * @return boolean
     */
    public static boolean isInheritanceChanged(Y9ResourceBase originResourceBase, Y9ResourceBase updateResourceBase) {
        return !Objects.equals(originResourceBase.getInherit(), updateResourceBase.getInherit());
    }

}
