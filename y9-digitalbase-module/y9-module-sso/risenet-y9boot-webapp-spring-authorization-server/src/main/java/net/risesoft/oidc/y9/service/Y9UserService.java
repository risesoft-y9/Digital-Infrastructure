package net.risesoft.oidc.y9.service;

import net.risesoft.oidc.y9.entity.Y9User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface Y9UserService {
    Y9User loadUserByUsername(String username);

}
