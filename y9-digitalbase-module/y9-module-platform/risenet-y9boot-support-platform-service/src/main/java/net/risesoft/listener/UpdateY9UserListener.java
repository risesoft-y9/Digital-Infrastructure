package net.risesoft.listener;

import java.util.Objects;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Manager;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.enums.platform.org.PersonTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.entity.user.Y9User;
import net.risesoft.y9public.service.tenant.Y9TenantService;
import net.risesoft.y9public.service.user.Y9UserService;

/**
 * 监听需要更新全局用户的事件并执行操作
 *
 * @author mengjuhua
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateY9UserListener {

    private final Y9TenantService y9TenantService;
    private final Y9UserService y9UserService;
    private final Y9PersonsToPositionsService y9PersonsToPositionsService;

    /**
     * 监听管理员添加事件
     * 
     * @param event 管理员添加事件
     */
    @EventListener
    @Transactional(readOnly = false)
    public void onY9ManagerCreated(Y9EntityCreatedEvent<Y9Manager> event) {
        Y9Manager y9Manager = event.getEntity();
        String personId = y9Manager.getId();
        String tenantId = y9Manager.getTenantId();
        LOGGER.info("开始处理管理员员新增->{}", y9Manager.getId());
        Y9Tenant y9Tenant = y9TenantService.getById(tenantId);
        Optional<Y9User> y9UserOptional = y9UserService.findByPersonIdAndTenantId(personId, tenantId);
        Y9User y9User;
        if (y9UserOptional.isEmpty()) {
            if (Boolean.TRUE.equals(y9Manager.getDisabled())) {
                return;
            }
            y9User = new Y9User();
            y9User.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        } else {
            y9User = y9UserOptional.get();
            if (Boolean.TRUE.equals(y9Manager.getDisabled())) {
                y9UserService.delete(y9User.getId());
                return;
            }
        }
        y9User.setTenantId(tenantId);
        y9User.setTenantName(y9Tenant.getName());
        y9User.setTenantShortName(y9Tenant.getShortName());
        y9User.setLoginName(y9Manager.getLoginName());
        y9User.setPassword(y9Manager.getPassword());
        y9User.setPersonId(y9Manager.getId());
        y9User.setEmail(y9Manager.getEmail());
        y9User.setMobile(y9Manager.getMobile());
        y9User.setSex(y9Manager.getSex());
        y9User.setCaid(null);
        y9User.setWeixinId(null);
        y9User.setGuidPath(y9Manager.getGuidPath());
        y9User.setOrderedPath(y9Manager.getOrderedPath());
        y9User.setDn(y9Manager.getDn());
        y9User.setManagerLevel(y9Manager.getManagerLevel());

        y9User.setName(y9Manager.getName());
        y9User.setParentId(y9Manager.getParentId());
        y9User.setAvator(y9Manager.getAvator());
        y9User.setPersonType(PersonTypeEnum.DEPARTMENT.getValue());
        y9User.setOriginal(true);
        y9User.setOriginalId(null);
        y9User.setGlobalManager(y9Manager.getGlobalManager());
        y9UserService.save(y9User);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新增管理员->{}执行完成", y9Manager.getId());
        }
    }

    /**
     * 监听管理员删除事件
     *
     * @param event 管理员删除事件
     */
    @EventListener
    @Transactional(readOnly = false)
    public void onY9ManagerDeleted(Y9EntityDeletedEvent<Y9Manager> event) {
        Y9Manager y9Manager = event.getEntity();
        LOGGER.info("开始处理管理员员删除->{}", y9Manager.getId());
        Optional<Y9User> y9UserOptional =
            y9UserService.findByPersonIdAndTenantId(y9Manager.getId(), y9Manager.getTenantId());
        if (y9UserOptional.isPresent()) {
            y9UserService.delete(y9UserOptional.get().getId());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除管理员->{}执行完成", y9Manager.getId());
        }
    }

    /**
     * 监听管理员更新事件
     *
     * @param event 管理员更新事件
     */
    @EventListener
    @Transactional(readOnly = false)
    public void onY9ManagerUpdated(Y9EntityUpdatedEvent<Y9Manager> event) {
        Y9Manager y9Manager = event.getUpdatedEntity();
        String tenantId = y9Manager.getTenantId();
        LOGGER.info("开始处理管理员员修改->{}", y9Manager.getId());
        Y9Tenant y9Tenant = y9TenantService.getById(tenantId);
        Optional<Y9User> y9UserOptional = y9UserService.findByPersonIdAndTenantId(y9Manager.getId(), tenantId);
        Y9User y9User;
        if (y9UserOptional.isEmpty()) {
            if (Boolean.TRUE.equals(y9Manager.getDisabled())) {
                return;
            }
            y9User = new Y9User();
            y9User.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        } else {
            y9User = y9UserOptional.get();
            if (Boolean.TRUE.equals(y9Manager.getDisabled())) {
                y9UserService.delete(y9User.getId());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("删除管理员->{}执行完成", y9User.getPersonId());
                }
                return;
            }
        }
        y9User.setTenantId(tenantId);
        y9User.setTenantName(y9Tenant.getName());
        y9User.setTenantShortName(y9Tenant.getShortName());
        y9User.setLoginName(y9Manager.getLoginName());
        y9User.setPassword(y9Manager.getPassword());
        y9User.setPersonId(y9Manager.getId());
        y9User.setEmail(y9Manager.getEmail());
        y9User.setMobile(y9Manager.getMobile());
        y9User.setSex(y9Manager.getSex());
        y9User.setCaid(null);
        y9User.setWeixinId(null);
        y9User.setGuidPath(y9Manager.getGuidPath());
        y9User.setOrderedPath(y9Manager.getOrderedPath());
        y9User.setDn(y9Manager.getDn());
        y9User.setManagerLevel(y9Manager.getManagerLevel());

        y9User.setName(y9Manager.getName());
        y9User.setParentId(y9Manager.getParentId());
        y9User.setAvator(y9Manager.getAvator());
        y9User.setPersonType(PersonTypeEnum.DEPARTMENT.getValue());
        y9User.setOriginal(true);
        y9User.setOriginalId(null);
        y9User.setGlobalManager(y9Manager.getGlobalManager());
        y9UserService.save(y9User);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新管理员->{}执行完成", y9Manager.getId());
        }
    }

    /**
     * 监听人员添加事件
     *
     * @param event 人员添加事件
     */
    @EventListener
    @Transactional(readOnly = false)
    public void onY9PersonCreated(Y9EntityCreatedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        String personId = person.getId();
        LOGGER.info("开始处理人员新增->{}", person.getId());
        String tenantId = person.getTenantId();
        Y9Tenant y9Tenant = y9TenantService.getById(tenantId);
        Optional<Y9User> y9UserOptional = y9UserService.findByPersonIdAndTenantId(personId, tenantId);
        Y9User y9User;
        if (y9UserOptional.isEmpty()) {
            if (Boolean.TRUE.equals(person.getDisabled())) {
                return;
            }

            y9User = new Y9User();
            y9User.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        } else {
            y9User = y9UserOptional.get();
            if (Boolean.TRUE.equals(person.getDisabled())) {
                y9UserService.delete(y9User.getId());
                return;
            }
        }

        y9User.setTenantId(tenantId);
        y9User.setTenantName(y9Tenant.getName());
        y9User.setTenantShortName(y9Tenant.getShortName());
        y9User.setLoginName(person.getLoginName());
        y9User.setPassword(person.getPassword());
        y9User.setPersonId(person.getId());
        y9User.setEmail(person.getEmail());
        y9User.setMobile(person.getMobile());
        y9User.setSex(person.getSex());
        y9User.setCaid(person.getCaid());
        y9User.setWeixinId(person.getWeixinId());
        y9User.setGuidPath(person.getGuidPath());
        y9User.setOrderedPath(person.getOrderedPath());
        y9User.setDn(person.getDn());
        y9User.setManagerLevel(ManagerLevelEnum.GENERAL_USER);

        y9User.setName(person.getName());
        y9User.setParentId(person.getParentId());
        y9User.setAvator(person.getAvator());
        y9User.setPersonType(person.getPersonType());
        y9User.setOriginal(person.getOriginal());
        y9User.setOriginalId(person.getOriginalId());
        y9User.setGlobalManager(false);
        String positionIds = y9PersonsToPositionsService.getPositionIdsByPersonId(person.getId());
        y9User.setPositions(positionIds);
        y9UserService.save(y9User);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新增人员->{}执行完成", person.getId());
        }
    }

    /**
     * 监听人员删除事件
     *
     * @param event 人员删除事件
     */
    @EventListener
    @Transactional(readOnly = false)
    public void onY9PersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        LOGGER.info("开始处理人员删除->{}", person.getId());
        Optional<Y9User> y9UserOptional = y9UserService.findByPersonIdAndTenantId(person.getId(), person.getTenantId());
        if (y9UserOptional.isPresent()) {
            y9UserService.delete(y9UserOptional.get().getId());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除人员->{}执行完成", person.getId());
        }
    }

    /**
     * 监听人员更新事件
     *
     * @param event 人员更新事件
     */
    @EventListener
    @Transactional(readOnly = false)
    public void onY9PersonUpdated(Y9EntityUpdatedEvent<Y9Person> event) {
        Y9Person person = event.getUpdatedEntity();
        String tenantId = person.getTenantId();
        LOGGER.info("开始处理人员更新->{}", person.getId());
        Y9Tenant y9Tenant = y9TenantService.getById(tenantId);
        Optional<Y9User> y9UserOptional = y9UserService.findByPersonIdAndTenantId(person.getId(), tenantId);
        Y9User y9User;
        if (y9UserOptional.isEmpty()) {
            if (Boolean.TRUE.equals(person.getDisabled())) {
                return;
            }

            y9User = new Y9User();
            y9User.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        } else {
            y9User = y9UserOptional.get();
            if (Boolean.TRUE.equals(person.getDisabled())) {
                y9UserService.delete(y9User.getId());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("更新人员已删除，执行删除人员->{}执行完成", y9User.getPersonId());
                }
                return;
            }
        }

        y9User.setTenantId(tenantId);
        y9User.setTenantName(y9Tenant.getName());
        y9User.setTenantShortName(y9Tenant.getShortName());
        y9User.setLoginName(person.getLoginName());
        y9User.setPassword(person.getPassword());
        y9User.setPersonId(person.getId());
        y9User.setEmail(person.getEmail());
        y9User.setMobile(person.getMobile());
        y9User.setSex(person.getSex());
        y9User.setCaid(person.getCaid());
        y9User.setWeixinId(person.getWeixinId());
        y9User.setGuidPath(person.getGuidPath());
        y9User.setOrderedPath(person.getOrderedPath());
        y9User.setDn(person.getDn());
        y9User.setManagerLevel(ManagerLevelEnum.GENERAL_USER);

        y9User.setName(person.getName());
        y9User.setParentId(person.getParentId());
        y9User.setAvator(person.getAvator());
        y9User.setPersonType(person.getPersonType());
        y9User.setOriginal(person.getOriginal());
        y9User.setOriginalId(person.getOriginalId());
        y9User.setGlobalManager(false);
        String positionIds = y9PersonsToPositionsService.getPositionIdsByPersonId(person.getId());
        y9User.setPositions(positionIds);
        y9UserService.save(y9User);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新人员->{}执行完成", person.getId());
        }
    }

    /**
     * 监听人员-岗位关联新增事件 并更新用户拥有的岗位id
     *
     * @param event 人员-岗位关联新增事件
     */
    @EventListener
    @Transactional(readOnly = false)
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        Optional<Y9User> y9UserOptional = y9UserService.findByPersonIdAndTenantId(y9PersonsToPositions.getPersonId(),
            Y9LoginUserHolder.getTenantId());
        if (y9UserOptional.isPresent()) {
            Y9User y9User = y9UserOptional.get();
            String positionIds =
                y9PersonsToPositionsService.getPositionIdsByPersonId(y9PersonsToPositions.getPersonId());
            y9User.setPositions(positionIds);
            y9UserService.save(y9User);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("人员-岗位关联新增触发的更新用户拥有的岗位id完成");
        }
    }

    /**
     * 监听人员-岗位关联删除事件 并更新用户拥有的岗位id
     *
     * @param event 人员-岗位关联删除事件
     */
    @EventListener
    @Transactional(readOnly = false)
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        Optional<Y9User> y9UserOptional = y9UserService.findByPersonIdAndTenantId(y9PersonsToPositions.getPersonId(),
            Y9LoginUserHolder.getTenantId());
        if (y9UserOptional.isPresent()) {
            Y9User y9User = y9UserOptional.get();
            String positionIds =
                y9PersonsToPositionsService.getPositionIdsByPersonId(y9PersonsToPositions.getPersonId());
            y9User.setPositions(positionIds);
            y9UserService.save(y9User);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("人员-岗位关联删除触发的更新用户拥有的岗位id完成");
        }
    }

    /**
     * 监听租户更新事件
     *
     * @param event 租户更新事件
     */
    @EventListener
    @Transactional(readOnly = false)
    public void onY9TenantUpdated(Y9EntityUpdatedEvent<Y9Tenant> event) {
        Y9Tenant originEntity = event.getOriginEntity();
        Y9Tenant updatedEntity = event.getUpdatedEntity();
        LOGGER.info("租户[{}]更新触发的更新用户开始", updatedEntity.getId());

        if (!Objects.equals(originEntity.getName(), updatedEntity.getName())) {
            y9UserService.updateByTenantId(updatedEntity.getId(), updatedEntity.getName(),
                updatedEntity.getShortName());
        }

        LOGGER.info("租户[{}]更新触发的更新用户结束", updatedEntity.getId());
    }
}
