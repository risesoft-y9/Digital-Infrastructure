package net.risesoft.y9public.service;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.y9public.entity.Tips;

public interface TipsService {

    void deleteAll();

    void deleteById(String id);

    List<Tips> findAll();

    Tips getTipsById(String id);

    Page<Tips> getTipsList(Integer page, Integer limit);

    Tips saveOrUpdate(String guid, String userId, String message, String link, String createtime);

    Tips saveOrUpdate(Tips tips);
}
