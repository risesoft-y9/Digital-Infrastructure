package net.risesoft.log.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.entity.Y9logIpDeptMapping;
import net.risesoft.log.repository.Y9logIpDeptMappingRepository;
import net.risesoft.log.service.Y9logIpDeptMappingService;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9logIpDeptMappingServiceImpl implements Y9logIpDeptMappingService {
    private final Y9logIpDeptMappingRepository y9logIpDeptMappingRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Y9logIpDeptMapping getById(String id) {
        return y9logIpDeptMappingRepository.findById(id).orElse(null);
    }

    @Override
    public List<Y9logIpDeptMapping> listAll() {
        List<Y9logIpDeptMapping> list = new ArrayList<>();
        Iterable<Y9logIpDeptMapping> ipDeptIterable =
            y9logIpDeptMappingRepository.findAll(Sort.by(Direction.DESC, "tabIndex"));
        Iterator<Y9logIpDeptMapping> iterator = ipDeptIterable.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    @Override
    public List<Y9logIpDeptMapping> listAllOrderByClientIpSection() {
        List<Y9logIpDeptMapping> list = new ArrayList<>();
        Iterable<Y9logIpDeptMapping> ipDeptIterable =
            y9logIpDeptMappingRepository.findAll(Sort.by(Sort.Direction.ASC, "clientIpSection"));
        Iterator<Y9logIpDeptMapping> iterator = ipDeptIterable.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    @Override
    public List<Y9logIpDeptMapping> listByClientIpSection(String clientIpSection) {
        return y9logIpDeptMappingRepository.findByClientIpSection(clientIpSection);
    }

    @Override
    public List<String> listClientIpSections() {
        List<String> clientIpSectionList = this.listAllOrderByClientIpSection().stream()
            .map(Y9logIpDeptMapping::getClientIpSection).collect(Collectors.toList());
        return clientIpSectionList;
    }

	@Override
	public Y9Page<Y9logIpDeptMapping> pageSearchList(int page, int rows, String clientIpSection, String deptName) {
		// TODO Auto-generated method stub
		return null;
	}
	
    /*
    @Override
    public Y9Page<Y9logIpDeptMapping> pageSearchList(int page, int rows, String clientIp4Abc, String deptName) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.IP_DEPT_MAPPING_INDEX);

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery("clientIpSection"));
        if (StringUtils.isNotBlank(deptName)) {
            query.must(QueryBuilders.wildcardQuery("deptName", "*" + deptName + "*"));
        }
        if (StringUtils.isNotBlank(clientIp4Abc)) {
            query.must(QueryBuilders.wildcardQuery("clientIpSection", "*" + clientIp4Abc + "*"));
        }
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(query)
            .withPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows))
            .withSorts(SortBuilders.fieldSort("clientIpSection").order(SortOrder.ASC)).build();
        SearchHits<Y9logIpDeptMapping> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logIpDeptMapping.class, index);

        List<Y9logIpDeptMapping> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total, list);
    }*/

    @Override
    public void removeOrganWords(String[] ipDeptMappingIds) {
        for (String id : ipDeptMappingIds) {
            y9logIpDeptMappingRepository.deleteById(id);
        }
    }

    @Override
    public void save(Y9logIpDeptMapping y9logIpDeptMapping) {
        y9logIpDeptMappingRepository.save(y9logIpDeptMapping);
    }

    @Override
    public Y9logIpDeptMapping saveOrUpdate(Y9logIpDeptMapping y9logIpDeptMapping) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNoneBlank(y9logIpDeptMapping.getId())) {
            y9logIpDeptMapping.setOperator(Y9LoginUserHolder.getUserInfo().getName());
            y9logIpDeptMapping.setUpdateTime(sdf.format(new Date()));
            y9logIpDeptMappingRepository.save(y9logIpDeptMapping);
            return y9logIpDeptMapping;
        }

        y9logIpDeptMapping.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        y9logIpDeptMapping.setOperator(Y9LoginUserHolder.getUserInfo().getName());
        y9logIpDeptMapping.setSaveTime(sdf.format(new Date()));
        y9logIpDeptMapping.setUpdateTime(sdf.format(new Date()));

        Pageable pageable = PageRequest.of(0, 1, Direction.DESC, "tabIndex");
        Page<Y9logIpDeptMapping> page = y9logIpDeptMappingRepository.findAll(pageable);
        Y9logIpDeptMapping dm = page.getContent().isEmpty() ? null : page.getContent().get(0);
        Integer tabIndex = dm != null ? dm.getTabIndex() : null;
        if (tabIndex == null) {
            y9logIpDeptMapping.setTabIndex(0);
        } else {
            y9logIpDeptMapping.setTabIndex(tabIndex + 1);
        }
        y9logIpDeptMappingRepository.save(y9logIpDeptMapping);
        return y9logIpDeptMapping;
    }

    @Override
    public void update4Order(String[] idAndTabIndexs) {
        try {
            for (String s : idAndTabIndexs) {
                String[] arr = s.split(":");
                Y9logIpDeptMapping y9logIpDeptMapping = y9logIpDeptMappingRepository.findById(arr[0]).orElse(null);
                if (y9logIpDeptMapping != null) {
                    y9logIpDeptMapping.setTabIndex(Integer.parseInt(arr[1]));
                    y9logIpDeptMappingRepository.save(y9logIpDeptMapping);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
