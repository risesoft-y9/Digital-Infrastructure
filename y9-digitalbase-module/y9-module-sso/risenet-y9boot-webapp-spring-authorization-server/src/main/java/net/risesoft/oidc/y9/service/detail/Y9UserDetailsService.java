package net.risesoft.oidc.y9.service.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.risesoft.oidc.util.Y9BeanUtil;
import net.risesoft.oidc.y9.entity.Y9User;
import net.risesoft.oidc.y9.service.Y9UserService;

@Service
public class Y9UserDetailsService implements UserDetailsService {
    @Autowired
    private Y9UserService y9UserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Y9User y9User = y9UserService.loadUserByUsername(username);
        if (y9User == null) {
            throw new UsernameNotFoundException("Could not find user");
        }

        Y9UserDetails y9UserDetails = new Y9UserDetails();
        Y9BeanUtil.copyProperties(y9User, y9UserDetails);
        y9UserDetails.setEnabled(true).setAccountNonExpired(true).setAccountNonLocked(true)
            .setCredentialsNonExpired(true).setEnabled(true);

        List<GrantedAuthority> authorities = new ArrayList<>();
        y9UserDetails.setAuthorities(Collections.unmodifiableSet(Y9UserDetails.sortAuthorities(authorities)));

        return y9UserDetails;
    }
}
