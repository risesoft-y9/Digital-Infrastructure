package net.risesoft.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

@Configuration
public class HistoryConfig {

    @Bean
    public AuthorProvider authorProvider() {
        return new AuthorProvider() {
            @Override
            public String provide() {
                UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
                if (userInfo != null) {
                    String author = userInfo.getPersonId();
                    return author;
                }
                return "";
            }
        };
    }

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provide() {
                Map<String, String> map = new HashMap<String, String>();
                String tenantId = Y9LoginUserHolder.getTenantId();
                String deptId = Y9LoginUserHolder.getDeptId();
                map.put("tenantId", tenantId);
                map.put("deptId", deptId);
                String userHostIp = "";
                try {
                    ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                    if (sra != null) {
                        HttpServletRequest request = sra.getRequest();
                        userHostIp = Y9Context.getIpAddr(request);
                        if (userHostIp != null && userHostIp.contains(":")) {
                            userHostIp = "127.0.0.1";
                        }
                        map.put("hostIp", userHostIp);
                    }
                    UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
                    if (userInfo != null) {
                        map.put("authorName", userInfo.getName());
                    }
                } catch (Exception e) {
                }
                return map;
            }
        };
    }

}
