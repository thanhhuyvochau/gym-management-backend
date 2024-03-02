package spring.project.base.util.mapper;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import spring.project.base.config.security.oauth2.dto.LocalUser;
import spring.project.base.config.security.oauth2.dto.UserInfo;
import spring.project.base.constant.SocialProvider;
import spring.project.base.entity.Account;
import spring.project.base.entity.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneralUtils {

    public static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Role role) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode().name()));
        return authorities;
    }

    public static SocialProvider toSocialProvider(String providerId) {
        for (SocialProvider socialProvider : SocialProvider.values()) {
            if (socialProvider.getProviderType().equals(providerId)) {
                return socialProvider;
            }
        }
        return SocialProvider.LOCAL;
    }

    public static UserInfo buildUserInfo(LocalUser localUser) {
        List<String> roles =
                localUser.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
        Account user = localUser.getAccount();
        return new UserInfo(user.getId().toString(), user.getEmail(), roles);
    }
}