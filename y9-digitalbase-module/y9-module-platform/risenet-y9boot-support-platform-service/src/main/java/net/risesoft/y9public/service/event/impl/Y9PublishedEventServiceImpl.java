package net.risesoft.y9public.service.event.impl;

import java.util.Date;
import java.util.List;

import net.risesoft.util.PlatformModelConvertUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.platform.PublishedEvent;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.PublishedEventQuery;
import net.risesoft.y9public.entity.event.Y9PublishedEvent;
import net.risesoft.y9public.repository.event.Y9PublishedEventRepository;
import net.risesoft.y9public.service.event.Y9PublishedEventService;
import net.risesoft.y9public.specification.Y9PublishedEventSpecification;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9PublishedEventServiceImpl implements Y9PublishedEventService {

    private final Y9PublishedEventRepository y9PublishedEventRepository;

    @Override
    public List<PublishedEvent> listByTenantId(String tenantId, Date startTime) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        PublishedEventQuery query = PublishedEventQuery.builder().tenantId(tenantId).startTime(startTime).build();
        Y9PublishedEventSpecification spec = new Y9PublishedEventSpecification(query);
        List<Y9PublishedEvent> y9PublishedEventList = y9PublishedEventRepository.findAll(spec, sort);
        return entityToModel(y9PublishedEventList);
    }

    @Override
    public Y9Page<PublishedEvent> page(Y9PageQuery pageQuery, PublishedEventQuery query) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Y9PublishedEventSpecification spec = new Y9PublishedEventSpecification(query);
        Page<Y9PublishedEvent> y9PublishedEventPage = y9PublishedEventRepository.findAll(spec, pageable);

        return Y9Page.success(pageQuery.getPage(), y9PublishedEventPage.getTotalPages(),
            y9PublishedEventPage.getTotalElements(), entityToModel(y9PublishedEventPage.getContent()));
    }

    private static List<PublishedEvent> entityToModel(List<Y9PublishedEvent> y9PublishedEventList) {
        return PlatformModelConvertUtil.convert(y9PublishedEventList, PublishedEvent.class);
    }
}
