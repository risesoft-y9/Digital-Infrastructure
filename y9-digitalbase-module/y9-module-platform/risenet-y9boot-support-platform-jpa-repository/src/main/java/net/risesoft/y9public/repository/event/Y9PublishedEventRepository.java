package net.risesoft.y9public.repository.event;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.risesoft.y9public.entity.event.Y9PublishedEvent;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9PublishedEventRepository
    extends JpaRepository<Y9PublishedEvent, String>, JpaSpecificationExecutor<Y9PublishedEvent> {

    List<Y9PublishedEvent> findByTenantId(String tenantId, Sort sort);
}
