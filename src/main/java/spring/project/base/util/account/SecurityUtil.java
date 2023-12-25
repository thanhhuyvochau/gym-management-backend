package spring.project.base.util.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import spring.project.base.config.security.oauth2.dto.LocalUser;
import spring.project.base.config.security.service.UserDetailsImpl;
import spring.project.base.entity.Account;
import spring.project.base.common.ApiException;
import spring.project.base.repository.AccountRepository;
import spring.project.base.util.message.MessageUtil;

import java.util.Map;
import java.util.Optional;

@Component
public class SecurityUtil {
    private static MessageUtil messageUtil;
    private static AccountRepository staticAccountRepository;
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public SecurityUtil(MessageUtil messageUtil, AccountRepository accountRepository) {
        this.messageUtil = messageUtil;
        staticAccountRepository = accountRepository;
    }

    public static Account getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Object principal = securityContext.getAuthentication().getPrincipal();
        Account account = null;
        String email = getEmailCurrentUser(principal);
        account = staticAccountRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.create(HttpStatus.NOT_FOUND)
                        .withMessage("Tài khoản đăng nhập hiện tại không tìm thấy " + email == null ? "": email));
        return account;
    }

    public static Optional<Account> getCurrentUserOptional() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Object principal = securityContext.getAuthentication().getPrincipal();
        Account account = null;
        String email = getEmailCurrentUser(principal);
        account = staticAccountRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.create(HttpStatus.NOT_FOUND)
                        .withMessage("Tài khoản đăng nhập hiện tại không tìm thấy " + email == null ? "" : email));
        return Optional.ofNullable(account);
    }

    public static Account hasCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Object principal = securityContext.getAuthentication().getPrincipal();
        String email = getEmailCurrentUser(principal);
        Account account = staticAccountRepository.findByEmail(email).orElse(null);

        return account;
    }

    public static Optional<String> getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(getEmailCurrentUser(authentication.getPrincipal()));
        }
    }

    public static Account getUserOrThrowException(Optional<Account> userOptional) {
        return userOptional.orElseThrow(() -> ApiException.create(HttpStatus.UNAUTHORIZED).withMessage("Người dùng chưa đăng nhập hoặc không tồn tại"));
    }

    public static String getEmailCurrentUser(Object principal) {
        String email = null;
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) principal;
            email = userPrincipal.getEmail();
        } else if (principal instanceof LocalUser) {
            LocalUser userPrincipal = (LocalUser) principal;
            Map<String, Object> attributes = userPrincipal.getAttributes();
            email = (String) attributes.get("email");
        } else {
            logger.error("Cannot cast Authentication to Classes is supported by application");
        }
        return email;
    }
}
