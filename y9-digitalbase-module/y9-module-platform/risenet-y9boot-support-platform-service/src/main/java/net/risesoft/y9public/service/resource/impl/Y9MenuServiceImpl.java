package net.risesoft.y9public.service.resource.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.AuditLogEnum;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.manager.resource.Y9MenuManager;
import net.risesoft.y9public.repository.resource.Y9MenuRepository;
import net.risesoft.y9public.service.resource.Y9MenuService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Y9MenuServiceImpl implements Y9MenuService {

    private final Y9MenuRepository y9MenuRepository;

    private final Y9MenuManager y9MenuManager;

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(List<String> idList) {
        for (String id : idList) {
            this.delete(id);
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(String id) {
        Y9Menu y9Menu = y9MenuManager.getById(id);
        y9MenuManager.delete(y9Menu);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MENU_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MENU_DELETE.getDescription(), y9Menu.getName()))
            .objectId(id)
            .oldObject(y9Menu)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Menu));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public List<Y9Menu> disable(List<String> idList) {
        List<Y9Menu> y9MenuList = new ArrayList<>();
        for (String id : idList) {
            y9MenuList.add(this.disable(id));
        }
        return y9MenuList;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9Menu disable(String id) {
        Y9Menu currentMenu = this.getById(id);
        Y9Menu originalMenu = Y9ModelConvertUtil.convert(currentMenu, Y9Menu.class);

        currentMenu.setEnabled(Boolean.FALSE);
        Y9Menu savedMenu = y9MenuManager.update(currentMenu);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MENU_UPDATE_ENABLE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.MENU_UPDATE_ENABLE.getDescription(), savedMenu.getName(), "禁用"))
            .objectId(id)
            .oldObject(originalMenu)
            .currentObject(savedMenu)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedMenu;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public List<Y9Menu> enable(List<String> idList) {
        List<Y9Menu> y9MenuList = new ArrayList<>();
        for (String id : idList) {
            y9MenuList.add(this.enable(id));
        }
        return y9MenuList;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9Menu enable(String id) {
        Y9Menu currentMenu = this.getById(id);
        Y9Menu originalMenu = Y9ModelConvertUtil.convert(currentMenu, Y9Menu.class);

        currentMenu.setEnabled(Boolean.TRUE);
        Y9Menu savedMenu = y9MenuManager.update(currentMenu);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MENU_UPDATE_ENABLE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.MENU_UPDATE_ENABLE.getDescription(), savedMenu.getName(), "启用"))
            .objectId(id)
            .oldObject(originalMenu)
            .currentObject(savedMenu)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedMenu;
    }

    @Override
    public boolean existsById(String id) {
        return y9MenuRepository.existsById(id);
    }

    @Override
    public Optional<Y9Menu> findById(String id) {
        return y9MenuManager.findByIdFromCache(id);
    }

    @Override
    public List<Y9Menu> findByNameLike(String name) {
        return y9MenuRepository.findByNameContainingOrderByTabIndex(name);
    }

    @Override
    public Y9Menu getById(String id) {
        return y9MenuManager.getByIdFromCache(id);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9Menu saveOrUpdate(Y9Menu y9Menu) {
        if (StringUtils.isNotBlank(y9Menu.getId())) {
            Optional<Y9Menu> y9MenuOptional = y9MenuManager.findById(y9Menu.getId());
            if (y9MenuOptional.isPresent()) {
                Y9Menu originMenu = Y9ModelConvertUtil.convert(y9MenuOptional.get(), Y9Menu.class);
                Y9Menu savedMenu = y9MenuManager.update(y9Menu);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.MENU_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.MENU_UPDATE.getDescription(), savedMenu.getName()))
                    .objectId(savedMenu.getId())
                    .oldObject(originMenu)
                    .currentObject(savedMenu)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedMenu;
            }
        }

        Y9Menu savedMenu = y9MenuManager.insert(y9Menu);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MENU_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MENU_CREATE.getDescription(), savedMenu.getName()))
            .objectId(savedMenu.getId())
            .oldObject(null)
            .currentObject(savedMenu)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedMenu;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9Menu updateTabIndex(String id, int index) {
        return y9MenuManager.updateTabIndex(id, index);
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    private void deleteByParentId(String parentId) {
        List<Y9Menu> y9MenuList = this.findByParentId(parentId);
        for (Y9Menu y9Menu : y9MenuList) {
            delete(y9Menu.getId());
        }
    }

    @Override
    public List<Y9Menu> findByParentId(String parentId) {
        return y9MenuRepository.findByParentIdOrderByTabIndex(parentId);
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App entity = event.getEntity();
        this.deleteByParentId(entity.getId());
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onMenuDeleted(Y9EntityDeletedEvent<Y9Menu> event) {
        Y9Menu entity = event.getEntity();
        this.deleteByParentId(entity.getId());
    }

}
