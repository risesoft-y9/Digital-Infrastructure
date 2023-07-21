package net.risesoft.y9.pubsub.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * 实体删除事件
 *
 * @author shidaobang
 * @date 2022/12/01
 */
public class Y9EntityDeletedEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {

    private static final long serialVersionUID = 2186781181314466191L;
    
    private final T entity;
    
    public Y9EntityDeletedEvent(T entity) {
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