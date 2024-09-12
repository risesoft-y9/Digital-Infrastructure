package net.risesoft.y9public.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9public.entity.Tips;
import net.risesoft.y9public.repository.TipsRespository;
import net.risesoft.y9public.service.TipsService;

@Service(value = "tipsService")
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public class TipsServiceImpl implements TipsService {

    @Autowired
    private TipsRespository tipsRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteAll() {
        tipsRepository.deleteAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteById(String id) {
        tipsRepository.deleteById(id);
    }

    @Override
    public List<Tips> findAll() {
        return tipsRepository.findAll();
    }

    @Override
    public Tips getTipsById(String id) {
        return tipsRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Tips> getTipsList(Integer page, Integer limit) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, limit, sort);
        return tipsRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Tips saveOrUpdate(String id, String userId, String message, String link, String createTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Tips tips = tipsRepository.findById(id).orElse(null);
        if (null == tips) {
            tips = new Tips();
            if (StringUtils.isBlank(id)) {
                tips.setId(Y9IdGenerator.genId());
            } else {
                tips.setId(id);
            }
            tips.setUserId(userId);
        }
        tips.setMessage(message);
        tips.setLink(link);
        if (StringUtils.isNoneBlank(createTime)) {
            try {
                tips.setCreateTime(sdf.parse(createTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tips.setCreateTime(new Date());
        }
        tips = tipsRepository.save(tips);
        return tips;
    }

    @Override
    @Transactional(readOnly = false)
    public Tips saveOrUpdate(Tips tips) {
        if (StringUtils.isBlank(tips.getId())) {
            tips.setId(Y9IdGenerator.genId());
        }
        tips = tipsRepository.save(tips);
        return tips;
    }

}
