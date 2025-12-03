package net.risesoft.org;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.Y9PublishService;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

@RestController
@RequestMapping("/")
public class MainController {

    @Resource(name = "y9PublishService")
    private Y9PublishService y9PublishService;

    /**
     * 发布新增事件
     */
    @GetMapping(value = "/publish")
    public String publishSyncDataSourceEvent() {
        Organization org = new Organization();
        org.setId("1");
        org.setName("1");
        org.setOrgType(OrgTypeEnum.ORGANIZATION);
        org.setTabIndex(1);
        Y9MessageOrg<OrgUnit> msg =
            new Y9MessageOrg<>(org, Y9OrgEventTypeConst.ORGANIZATION_ADD, Y9LoginUserHolder.getTenantId());
        y9PublishService.publishMessageOrg(msg);
        return "publish success";
    }
}
