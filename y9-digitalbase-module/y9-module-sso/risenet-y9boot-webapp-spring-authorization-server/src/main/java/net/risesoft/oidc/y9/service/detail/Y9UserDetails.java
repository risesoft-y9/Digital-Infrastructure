package net.risesoft.oidc.y9.service.detail;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.oidc.y9.entity.Y9User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

@JsonRootName("y9UserDetails")
@JsonAutoDetect(
		fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC
)
@JsonIgnoreProperties(
		ignoreUnknown = true
)
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Slf4j
@Accessors(chain = true)
public class Y9UserDetails extends Y9User implements UserDetails {
	@Serial
	private static final long serialVersionUID = 1095290600488048713L;

	protected Set<GrantedAuthority> authorities;

	protected boolean accountNonExpired;

	protected boolean accountNonLocked;

	protected boolean credentialsNonExpired;

	protected boolean enabled;

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.loginName;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Y9UserDetails user) {
			return this.loginName.equals(user.getUsername());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.loginName.hashCode();
	}

	public static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
		Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
		// Ensure array iteration order is predictable (as per
		// UserDetails.getAuthorities() contract and SEC-717)
		SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());
		for (GrantedAuthority grantedAuthority : authorities) {
			Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
			sortedAuthorities.add(grantedAuthority);
		}
		return sortedAuthorities;
	}

	private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {

		private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

		@Override
		public int compare(GrantedAuthority g1, GrantedAuthority g2) {
			// Neither should ever be null as each entry is checked before adding it to
			// the set. If the authority is null, it is a custom authority and should
			// precede others.
			if (g2.getAuthority() == null) {
				return -1;
			}
			if (g1.getAuthority() == null) {
				return 1;
			}
			return g1.getAuthority().compareTo(g2.getAuthority());
		}

	}

}
