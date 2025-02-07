package net.risesoft.oidc.service;

import net.risesoft.oidc.entity.Y9User;
import net.risesoft.oidc.repository.Y9UserRepository;
import net.risesoft.oidc.util.Y9BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class Y9UserDetailsService implements UserDetailsService {
    @Autowired
    private Y9UserRepository y9UserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Y9User y9User = y9UserRepository.getUserByUsername(username);
        if (y9User == null) {
            throw new UsernameNotFoundException("Could not find user");
        }

        /*UserDetails userDetails = User.withUsername(y9User.getLoginName())
                .password(y9User.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
        return userDetails;*/

        Y9UserDetails y9UserDetails = new Y9UserDetails();
        Y9BeanUtil.copyProperties(y9User, y9UserDetails);

        y9UserDetails
                .setEnabled(true)
                .setAccountNonExpired(true)
                .setAccountNonLocked(true)
                .setCredentialsNonExpired(true)
                .setEnabled(true);

        List<GrantedAuthority> authorities = new ArrayList<>();
        String roles = y9User.getRoles();
        if (roles == null || roles.isBlank()) {
            authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("read,admin");
        } else {
            String[] rolesArray = roles.split(",");
            for (String role : rolesArray) {
                if (role != null && role.trim().length() > 0) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }
        }
        y9UserDetails.setAuthorities(Collections.unmodifiableSet(Y9UserDetails.sortAuthorities(authorities)));

        return y9UserDetails;
    }
}
