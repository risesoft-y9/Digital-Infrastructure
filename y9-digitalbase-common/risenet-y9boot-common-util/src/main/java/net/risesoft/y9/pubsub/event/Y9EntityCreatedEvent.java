package net.risesoft.y9.pubsub.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * 实体新建事件
 *
 * @author shidaobang
 * @date 2022/12/29
 */
public class Y9EntityCreatedEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {

    private static final long serialVersionUID = -178737462829509713L;

    private final T entity;

    public Y9EntityCreatedEvent(T entity) {
        super(entity);
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getSource()));
    }
}
