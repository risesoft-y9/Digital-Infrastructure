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
public class Y9EntityUpdatedEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {

    private static final long serialVersionUID = 8544144545521310870L;

    private final T originEntity;
    private final T updatedEntity;
    
    public Y9EntityUpdatedEvent(T originEntity, T updatedEntity) {
        super(updatedEntity);
        this.originEntity = originEntity;
        this.updatedEntity = updatedEntity;
    }

    public T getOriginEntity() {
        return originEntity;
    }

    public T getUpdatedEntity() {
        return updatedEntity;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getSource()));
    }
}