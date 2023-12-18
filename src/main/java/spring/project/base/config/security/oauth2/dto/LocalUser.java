package spring.project.base.config.security.oauth2.dto;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.project.base.entity.Account;
import spring.project.base.util.mapper.GeneralUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author Chinna
 */
public class LocalUser extends User implements OAuth2User, OidcUser {

    /**
     *
     */
    private static final long serialVersionUID = -2845160792248762779L;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;
    private Map<String, Object> attributes;
    private Account account;

    public LocalUser(final String userID, final String password, final boolean enabled,
                     final boolean accountNonExpired, final boolean credentialsNonExpired,
                     final boolean accountNonLocked, final Collection<? extends GrantedAuthority> authorities,
                     final Account account) {
        this(userID, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities,
                account, null, null);
    }

    public LocalUser(final String userID, final String password, final boolean enabled,
                     final boolean accountNonExpired, final boolean credentialsNonExpired,
                     final boolean accountNonLocked, final Collection<? extends GrantedAuthority> authorities,
                     final Account account, OidcIdToken idToken,
                     OidcUserInfo userInfo) {
        super(userID, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.account = account;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    public static LocalUser create(Account account, Map<String, Object> attributes, OidcIdToken idToken,
                                   OidcUserInfo userInfo) {
        LocalUser localUser = new LocalUser(account.getEmail(), account.getPassword(), account.getStatus(), true,
                true, true,
                GeneralUtils.buildSimpleGrantedAuthorities(account.getRole()),
                account, idToken, userInfo);
        localUser.setAttributes(attributes);
        return localUser;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return this.userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.idToken;
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public String getName() {
        return null;
    }
}
