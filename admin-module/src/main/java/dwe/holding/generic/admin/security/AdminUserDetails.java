package dwe.holding.generic.admin.security;

import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.model.UserPreferences;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AdminUserDetails extends org.springframework.security.core.userdetails.User {

    private User user;
    private UserPreferences userPref;

    public AdminUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserPreferences getUserPref() {
        return userPref;
    }

    public void setUserPref(UserPreferences userPref) {
        this.userPref = userPref;
    }
}