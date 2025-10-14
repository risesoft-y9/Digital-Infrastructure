package y9;

import java.time.Instant;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.entity.Y9User;
import y9.service.Y9UserService;
import y9.util.Y9MessageDigest;

/**
 * 用于生成测试的用户
 * 
 * @author dingzhaojun
 * @date 2023/8/13
 * @since 9.6.3
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {
    private final Y9UserService y9UserService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Y9User y9User = null;
        try {
            y9User = y9UserService.findByPersonIdAndTenantId("11111111-1111-1111-1111-111111111117",
                "11111111-1111-1111-1111-111111111113");
        } catch (Exception e) {
            // e.printStackTrace();
        }
        if (y9User == null) {
            y9User = new Y9User();
            y9User.setId("11111111-1111-1111-1111-111111111117");
            y9User.setPersonId("11111111-1111-1111-1111-111111111117");
            y9User.setParentId("11111111-1111-1111-1111-111111111115");
            y9User.setTenantId("11111111-1111-1111-1111-111111111113");
            y9User.setDn("cn=系统管理员,o=组织");
            y9User.setGlobalManager(true);
            y9User.setGuidPath("11111111-1111-1111-1111-111111111115,11111111-1111-1111-1111-111111111117");
            y9User.setLoginName("systemManager");
            y9User.setManagerLevel(1);
            y9User.setName("系统管理员");
            y9User.setOriginal(true);
            y9User.setPassword(Y9MessageDigest.bcrypt("Risesoft@2025"));
            y9User.setPersonType("Manager");
            y9User.setSex(1);
            y9User.setTenantName("default");
            y9User.setTenantShortName("default");
            y9User.setCreateTime(Instant.now());
            y9User.setUpdateTime(Instant.now());
            y9User.setPositions("0000");
            y9UserService.save(y9User);
        }

        try {
            y9User = y9UserService.findByPersonIdAndTenantId("11111111-1111-1111-1111-111111111118",
                "11111111-1111-1111-1111-111111111113");
        } catch (Exception e) {
            // e.printStackTrace();
        }
        if (y9User == null) {
            y9User = new Y9User();
            y9User.setId("11111111-1111-1111-1111-111111111118");
            y9User.setPersonId("11111111-1111-1111-1111-111111111117");
            y9User.setParentId("11111111-1111-1111-1111-111111111115");
            y9User.setTenantId("11111111-1111-1111-1111-111111111113");
            y9User.setDn("cn=安全审计员,o=组织");
            y9User.setGlobalManager(true);
            y9User.setGuidPath("11111111-1111-1111-1111-111111111115,11111111-1111-1111-1111-111111111118");
            y9User.setLoginName("安全审计员");
            y9User.setManagerLevel(1);
            y9User.setName("安全审计员");
            y9User.setOriginal(true);
            y9User.setPassword(Y9MessageDigest.bcrypt("Risesoft@2025"));
            y9User.setPersonType("Manager");
            y9User.setSex(1);
            y9User.setTenantName("default");
            y9User.setTenantShortName("default");
            y9User.setCreateTime(Instant.now());
            y9User.setUpdateTime(Instant.now());
            y9User.setPositions("0001");
            y9UserService.save(y9User);
        }
    }
}
