package spring.project.base.config.security.oauth2.user;


import org.springframework.http.HttpStatus;
import spring.project.base.entity.common.ApiException;
import spring.project.base.entity.constant.SocialProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(SocialProvider.GOOGLE.getProviderType())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            throw ApiException.create(HttpStatus.FORBIDDEN).withMessage("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}