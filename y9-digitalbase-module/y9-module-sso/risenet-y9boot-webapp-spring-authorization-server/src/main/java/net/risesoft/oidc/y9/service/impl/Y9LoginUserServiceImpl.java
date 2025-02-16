package net.risesoft.oidc.y9.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.oidc.util.InetAddressUtil;
import net.risesoft.oidc.y9.entity.Y9User;
import net.risesoft.oidc.y9.repository.Y9UserRepository;
import net.risesoft.oidc.y9.service.Y9LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service("y9LoginUserService")
@Slf4j
@RequiredArgsConstructor
public class Y9LoginUserServiceImpl implements Y9LoginUserService {

    public static String SSO_SERVER_IP = InetAddressUtil.getLocalAddress().getHostAddress();

    @Autowired
    private final ConfigurableApplicationContext applicationContext;

    @Autowired
    private final Y9UserRepository y9UserRepository;

    @Override
    public void save(Y9User y9User, String success, String logMessage) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
