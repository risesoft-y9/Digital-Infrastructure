package net.risesoft.oidc.y9.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.oidc.y9.entity.Y9User;
import net.risesoft.oidc.y9.repository.Y9UserRepository;
import net.risesoft.oidc.y9.service.Y9UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        String[] arry = username.split("TTT");
        if (arry.length > 1) {
            List<Y9User> users = y9UserRepository.findByTenantShortNameAndLoginName(arry[0], arry[1]);
            if (users.size() > 0) {
                y9User = users.get(0);
            }
        } else {
            y9User = y9UserRepository.getUserByUsername(username);
        }

        return y9User;
    }

}
