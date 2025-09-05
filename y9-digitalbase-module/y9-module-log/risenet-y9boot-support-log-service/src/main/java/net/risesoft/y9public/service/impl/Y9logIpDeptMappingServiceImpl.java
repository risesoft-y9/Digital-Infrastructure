package net.risesoft.y9public.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.domain.Y9LogIpDeptMappingDO;
import net.risesoft.log.repository.Y9logIpDeptMappingCustomRepository;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.service.Y9logIpDeptMappingService;

/**
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Y9logIpDeptMappingServiceImpl implements Y9logIpDeptMappingService {

    private final Y9logIpDeptMappingCustomRepository y9logIpDeptMappingCustomRepository;

    @Override
    public List<Y9LogIpDeptMappingDO> listAllOrderByClientIpSection() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        boolean isOperation = tenantId.equals(InitDataConsts.OPERATION_TENANT_ID);

        if (isOperation) {
            return y9logIpDeptMappingCustomRepository.findAll(Sort.by(Sort.Direction.ASC, "clientIpSection"));
        } else {
            return y9logIpDeptMappingCustomRepository.findByTenantId(tenantId,
                Sort.by(Sort.Direction.ASC, "clientIpSection"));
        }
    }

    @Override
    public List<Y9LogIpDeptMappingDO> listByClientIpSection(String clientIpSection) {
        return y9logIpDeptMappingCustomRepository.findByClientIpSection(clientIpSection);
    }

    @Override
    public List<Y9LogIpDeptMappingDO> listByTenantIdAndClientIpSection(String tenantId, String clientIpSection) {
        return y9logIpDeptMappingCustomRepository.findByTenantIdAndClientIpSection(tenantId, clientIpSection);
    }

    @Override
    public List<String> listClientIpSections() {
        return this.listAllOrderByClientIpSection()
            .stream()
            .map(Y9LogIpDeptMappingDO::getClientIpSection)
            .collect(Collectors.toList());
    }

    @Override
    public Y9Page<Y9LogIpDeptMappingDO> pageSearchList(int page, int rows, String clientIp4Abc, String deptName) {
        return y9logIpDeptMappingCustomRepository.pageSearchList(page, rows, clientIp4Abc, deptName);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeOrganWords(String[] ipDeptMappingIds) {
        for (String id : ipDeptMappingIds) {
            y9logIpDeptMappingCustomRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9LogIpDeptMappingDO y9LogIpDeptMappingDO) {
        y9logIpDeptMappingCustomRepository.save(y9LogIpDeptMappingDO);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9LogIpDeptMappingDO saveOrUpdate(Y9LogIpDeptMappingDO y9LogIpDeptMappingDO) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNoneBlank(y9LogIpDeptMappingDO.getId())) {
            Y9LogIpDeptMappingDO oldMapping =
                y9logIpDeptMappingCustomRepository.findById(y9LogIpDeptMappingDO.getId()).orElse(null);
            if (oldMapping != null) {
                Y9BeanUtil.copyProperties(y9LogIpDeptMappingDO, oldMapping);
                oldMapping.setOperator(Y9LoginUserHolder.getUserInfo().getName());
                oldMapping.setUpdateTime(sdf.format(new Date()));
                return y9logIpDeptMappingCustomRepository.save(oldMapping);
            }
        }

        y9LogIpDeptMappingDO.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        y9LogIpDeptMappingDO.setOperator(Y9LoginUserHolder.getUserInfo().getName());
        y9LogIpDeptMappingDO.setSaveTime(sdf.format(new Date()));
        y9LogIpDeptMappingDO.setUpdateTime(sdf.format(new Date()));

        Pageable pageable = PageRequest.of(0, 1, Direction.DESC, "tabIndex");
        Page<Y9LogIpDeptMappingDO> page = y9logIpDeptMappingCustomRepository.page(pageable);
        Y9LogIpDeptMappingDO dm = page.getContent().isEmpty() ? null : page.getContent().get(0);
        Integer tabIndex = dm != null ? dm.getTabIndex() : null;
        if (tabIndex == null) {
            y9LogIpDeptMappingDO.setTabIndex(0);
        } else {
            y9LogIpDeptMappingDO.setTabIndex(tabIndex + 1);
        }
        return y9logIpDeptMappingCustomRepository.save(y9LogIpDeptMappingDO);
    }

    @Override
    @Transactional(readOnly = false)
    public void update4Order(String[] idAndTabIndexs) {
        try {
            for (String s : idAndTabIndexs) {
                String[] arr = s.split(":");
                Y9LogIpDeptMappingDO y9LogIpDeptMappingDO =
                    y9logIpDeptMappingCustomRepository.findById(arr[0]).orElse(null);
                if (y9LogIpDeptMappingDO != null) {
                    y9LogIpDeptMappingDO.setTabIndex(Integer.parseInt(arr[1]));
                    y9logIpDeptMappingCustomRepository.save(y9LogIpDeptMappingDO);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
