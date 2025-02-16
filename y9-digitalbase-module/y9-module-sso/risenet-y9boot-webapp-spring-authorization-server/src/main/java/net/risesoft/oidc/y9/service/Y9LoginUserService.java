package net.risesoft.oidc.y9.service;

import net.risesoft.oidc.y9.entity.Y9User;

public interface Y9LoginUserService {

    void save(Y9User y9User, String success, String logMessage);

}
