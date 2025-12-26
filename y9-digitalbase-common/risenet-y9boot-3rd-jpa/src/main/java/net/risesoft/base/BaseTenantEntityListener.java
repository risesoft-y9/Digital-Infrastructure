package net.risesoft.base;

import net.risesoft.y9.Y9LoginUserHolder;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * 租户实体监听器基类
 * <p>
 * 用于监听实体的持久化和更新事件，可用于在实体保存和更新前进行统一处理
 *
 * @author shidaobang
 * @date 2025/12/25
 */
public class BaseTenantEntityListener {
    /**
     * 在实体持久化之前执行的操作
     *
     * @param entity 要持久化的实体对象
     */
    @PrePersist
    public void prePersist(BaseTenantEntity entity) {
        entity.tenantId = Y9LoginUserHolder.getTenantId();
    }

    /**
     * 在实体更新之前执行的操作
     *
     * @param entity 要更新的实体对象
     */
    @PreUpdate
    public void preUpdate(BaseTenantEntity entity) {
        entity.tenantId = Y9LoginUserHolder.getTenantId();
    }
}