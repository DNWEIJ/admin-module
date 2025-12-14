package dwe.holding.admin.security;

import dwe.holding.admin.model.tenant.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class AdminUserDetails extends org.springframework.security.core.userdetails.User {

    private User user;
    private String tempGenericStorage;
    public AdminUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}