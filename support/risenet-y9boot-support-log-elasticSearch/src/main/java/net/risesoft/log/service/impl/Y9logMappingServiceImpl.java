package net.risesoft.log.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import net.risesoft.log.entity.Y9logMapping;
import net.risesoft.log.repository.Y9logMappingRepository;
import net.risesoft.log.service.Y9logMappingService;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@RequiredArgsConstructor
public class Y9logMappingServiceImpl implements Y9logMappingService {
    private final Y9logMappingRepository y9logMappingRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void deleteFieldMapping(String id) {
        y9logMappingRepository.deleteById(id);
    }

    @Override
    public String getCnModularName(String modularName) {
        List<Y9logMapping> list = y9logMappingRepository.findByModularName(modularName);
        if (list.isEmpty()) {
            return "";
        }
        return list.get(0).getModularCnName();
    }

    @Override
    public Y9logMapping getFieldMappingEntity(String id) {
        return y9logMappingRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Y9logMapping> page(int page, int rows, String sort) {
        if (sort != null && !"".equals(sort)) {
            Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, sort));
            return y9logMappingRepository.findAll(pageable);
        }
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, "modularName"));
        return y9logMappingRepository.findAll(pageable);
    }

	@Override
	public Page<Y9logMapping> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName) {
		// TODO Auto-generated method stub
		return null;
	}

    /*
    @Override
    public Page<Y9logMapping> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOG_MAPPING_INDEX);
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        Sort sort = Sort.by(Sort.Direction.DESC, "modularCnName");
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, sort);

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(modularCnName)) {
            query.must(QueryBuilders.termQuery("modularCnName", modularCnName));
        }
        if (StringUtils.isNotBlank(modularName)) {
            query.must(QueryBuilders.termQuery("modularName", modularName));
        }
        NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(query).withPageable(pageable).build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logMapping> searchHits = elasticsearchOperations.search(searchQuery, Y9logMapping.class, index);
        List<Y9logMapping> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logMapping> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    })*/

    @Override
    public void save(Y9logMapping y9logMapping) {
        y9logMappingRepository.save(y9logMapping);
    }

    @Override
    public List<Y9logMapping> validateName(String name) {
        return y9logMappingRepository.findByModularName(name);
    }

}
