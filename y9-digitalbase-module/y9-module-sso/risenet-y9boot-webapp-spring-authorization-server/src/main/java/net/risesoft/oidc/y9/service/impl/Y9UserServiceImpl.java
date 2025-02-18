package net.risesoft.oidc.y9.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.oidc.util.common.Base64Util;
import net.risesoft.oidc.y9.entity.Y9User;
import net.risesoft.oidc.y9.repository.Y9UserRepository;
import net.risesoft.oidc.y9.service.Y9UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("y9UserService")
@Slf4j
@RequiredArgsConstructor
public class Y9UserServiceImpl implements Y9UserService {
    @Autowired
    private final Y9UserRepository y9UserRepository;

    @Override
    public Y9User loadUserByUsername(String username) {
        Y9User y9User = null;
        List<Y9User> users;

        // loginType + ":::" + tenantShortName + ":::" + Username;
        String[] arry = username.split(":::");
        if (arry.length > 1) {
            users = y9UserRepository.findByTenantShortNameAndLoginName(arry[1], arry[2]);
        } else {
            users = y9UserRepository.findByLoginNameAndOriginal(username, true);
        }

        if (users.size() > 0) {
            y9User = users.get(0);
        }
        return y9User;
    }

}
