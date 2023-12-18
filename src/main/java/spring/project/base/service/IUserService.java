package spring.project.base.service;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import spring.project.base.config.security.oauth2.dto.LocalUser;
import spring.project.base.entity.dto.UserDto;
import spring.project.base.entity.request.ChangePasswordRequest;
import spring.project.base.entity.request.RegisterAccountRequest;
import spring.project.base.entity.response.VerifyResponse;

import java.io.IOException;
import java.util.Map;


public interface IUserService {
    UserDto getLoginUser();

    Long registerAccount(RegisterAccountRequest createAccountRequest);

    VerifyResponse verifyAccount(String code);

    Boolean resendVerifyEmail();

    LocalUser processUserRegistrationOAuth2(String registrationId, Map<String, Object> attributes,
                                            OidcIdToken idToken, OidcUserInfo userInfo) throws IOException;

    Long changePassword(ChangePasswordRequest changePasswordRequest);
}