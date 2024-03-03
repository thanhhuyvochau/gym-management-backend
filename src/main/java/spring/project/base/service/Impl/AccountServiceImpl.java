package spring.project.base.service.Impl;

import io.minio.ObjectWriteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import spring.project.base.common.ApiException;
import spring.project.base.common.ApiPage;
import spring.project.base.config.security.oauth2.dto.LocalUser;
import spring.project.base.config.security.oauth2.dto.SignUpRequest;
import spring.project.base.config.security.oauth2.user.OAuth2UserInfo;
import spring.project.base.config.security.oauth2.user.OAuth2UserInfoFactory;
import spring.project.base.constant.EAccountRole;
import spring.project.base.constant.EVerifyStatus;
import spring.project.base.dto.request.AccountFilterRequest;
import spring.project.base.dto.request.ChangePasswordRequest;
import spring.project.base.dto.request.RegisterAccountRequest;
import spring.project.base.dto.request.UpdateAccountRequest;
import spring.project.base.dto.response.UserResponse;
import spring.project.base.dto.response.VerifyResponse;
import spring.project.base.entity.Account;
import spring.project.base.entity.Role;
import spring.project.base.entity.Verification;
import spring.project.base.repository.AccountRepository;
import spring.project.base.repository.RoleRepository;
import spring.project.base.repository.VerificationRepository;
import spring.project.base.service.IAccountService;
import spring.project.base.util.account.PasswordUtil;
import spring.project.base.util.account.SecurityUtil;
import spring.project.base.util.adapter.MinioAdapter;
import spring.project.base.util.constant.Constants;
import spring.project.base.util.formater.MiniIOUtil;
import spring.project.base.util.formater.StringUtil;
import spring.project.base.util.formater.TimeUtil;
import spring.project.base.util.mapper.ConvertUtil;
import spring.project.base.util.mapper.GeneralUtils;
import spring.project.base.util.mapper.PageUtil;
import spring.project.base.util.message.EmailUtil;
import spring.project.base.util.message.MessageUtil;
import spring.project.base.util.specification.AccountSpecificationBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import static spring.project.base.util.constant.Constants.ErrorMessage.Invalid.INVALID_EMAIL;
import static spring.project.base.util.constant.Constants.ErrorMessage.Invalid.INVALID_PASSWORD;

@Service
@Transactional
public class AccountServiceImpl implements IAccountService {

    @Value("${minio.endpoint}")
    private String minioUrl;
    private final AccountRepository accountRepository;

    private final MessageUtil messageUtil;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final EmailUtil emailUtil;

    private final VerificationRepository verificationRepository;
    private final MinioAdapter minioAdapter;

    public AccountServiceImpl(AccountRepository accountRepository, MessageUtil messageUtil,
                              RoleRepository roleRepository, PasswordEncoder encoder, EmailUtil emailUtil,
                              VerificationRepository verificationRepository, MinioAdapter minioAdapter) {
        this.accountRepository = accountRepository;
        this.messageUtil = messageUtil;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.emailUtil = emailUtil;
        this.verificationRepository = verificationRepository;
        this.minioAdapter = minioAdapter;
    }

    private Account findUserById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> ApiException.create(HttpStatus.NOT_FOUND)
                .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.USER_NOT_FOUND_BY_ID) + id));
    }

    public Account getCurrentLoginUser() {
        return SecurityUtil.getCurrentUser();
    }

    @Override
    public UserResponse getLoginUser() {
        Account currentLoginAccount = getCurrentLoginUser();
        return ConvertUtil.convertUsertoUserResponse(currentLoginAccount);
    }

    @Override
    public Long changePassword(ChangePasswordRequest changePasswordRequest) {
        Account account = getCurrentLoginUser();
        if (account != null) {
            if (changePasswordRequest.getOldPassword().isEmpty() || changePasswordRequest.getNewPassword().isEmpty()) {
                throw ApiException.create(HttpStatus.BAD_REQUEST)
                        .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.Empty.EMPTY_PASSWORD));
            }

            if (!PasswordUtil.isValidPassword(changePasswordRequest.getNewPassword())) {
                throw ApiException.create(HttpStatus.BAD_REQUEST)
                        .withMessage(messageUtil.getLocalMessage(INVALID_PASSWORD));
            }
            if (!PasswordUtil.IsOldPassword(changePasswordRequest.getOldPassword(), account.getPassword())) {
                throw ApiException.create(HttpStatus.BAD_REQUEST)
                        .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.OLD_PASSWORD_MISMATCH));

            } else {
                if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())) {
                    throw ApiException.create(HttpStatus.BAD_REQUEST)
                            .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.NEW_PASSWORD_DUPLICATE));
                }
            }
            String encodedNewPassword = encoder.encode(changePasswordRequest.getNewPassword());
            account.setPassword(encodedNewPassword);
            account = accountRepository.save(account);
            return account.getId();
        } else {
            throw ApiException.create(HttpStatus.UNAUTHORIZED)
                    .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.USER_NOT_FOUND_BY_ID));
        }
    }

    public Long registerAccount(RegisterAccountRequest createAccountRequest) {
        validateCreateAccountRequest(createAccountRequest);
        Account account = new Account();
        if (Boolean.TRUE.equals(accountRepository.existsByEmail(createAccountRequest.getEmail()))) {
            throw ApiException.create(HttpStatus.BAD_REQUEST)
                    .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.REGISTERED_EMAIL)
                            + createAccountRequest.getEmail());
        }

//        if (Boolean.TRUE.equals(accountRepository.existsAccountByPhone(createAccountRequest.getPhone()))) {
//            throw ApiException.create(HttpStatus.BAD_REQUEST)
//                    .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.REGISTERED_PHONE_NUMBER)
//                            + createAccountRequest.getPhone());
//        }
        Role role = roleRepository.findRoleByCode(EAccountRole.GYM_OWNER)
                .orElseThrow(() -> ApiException.create(HttpStatus.NOT_FOUND)
                        .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.ROLE_NOT_FOUND_BY_CODE)
                                + EAccountRole.GYM_OWNER));
//        if (!TimeUtil.isValidBirthday(createAccountRequest.getBirthDay())) {
//            throw ApiException.create(HttpStatus.BAD_REQUEST)
//                    .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.Invalid.INVALID_BIRTH_DAY));
//        }
        account.setEmail(createAccountRequest.getEmail());
        account.setPassword(encoder.encode(createAccountRequest.getPassword()));
        account.setRole(role);
        account.setGender(createAccountRequest.getGender());
        account.setFullName(createAccountRequest.getFullName());
        account.setPhone(createAccountRequest.getPhone());
        account.setVerified(true);
        account.setStatus(true);
        account.setAddress(createAccountRequest.getAddress());
        Account savedAccount = accountRepository.save(account);

        // Send verify mail
        /**
         * emailUtil.sendVerifyEmailTo(savedAccount);
         * Notification notification =
         * NotificationDirector.buildRegisterSuccessAccount(account);
         * notification = notificationRepository.save(notification);
         * ResponseMessage responseMessage =
         * ConvertUtil.convertNotificationToResponseMessage(notification,
         * account);
         * webSocketUtil.sendPrivateNotification(createAccountRequest.getEmail(),
         * responseMessage);
         *
         */
        return savedAccount.getId();
    }

    public void validateCreateAccountRequest(RegisterAccountRequest request) {
        if (StringUtil.isNullOrEmpty(request.getEmail())) {
            throw ApiException.create(HttpStatus.BAD_REQUEST)
                    .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.Empty.EMPTY_EMAIL));
        }
        if (!StringUtil.isValidEmailAddress(request.getEmail())) {
            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage(messageUtil.getLocalMessage(INVALID_EMAIL));
        }
//        if (!StringUtil.isValidVietnameseMobilePhoneNumber(request.getPhone())) {
//            throw ApiException.create(HttpStatus.BAD_REQUEST)
//                    .withMessage(messageUtil.getLocalMessage(INVALID_PHONE_NUMBER));
//        }
//        if (StringUtil.isNullOrEmpty(request.getFullName())) {
//            throw ApiException.create(HttpStatus.BAD_REQUEST)
//                    .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.Empty.EMPTY_FULL_NAME));
//        }
//        boolean isValidGender = request.getGender().equals(EGenderType.MALE)
//                || request.getGender().equals(EGenderType.FEMALE);
//        if (!isValidGender) {
//            throw ApiException.create(HttpStatus.BAD_REQUEST).withMessage(messageUtil.getLocalMessage(INVALID_GENDER));
//        }
        if (!PasswordUtil.isValidPassword(request.getPassword())) {
            throw ApiException.create(HttpStatus.BAD_REQUEST)
                    .withMessage(messageUtil.getLocalMessage(INVALID_PASSWORD));
        }
    }

    @Override
    public VerifyResponse verifyAccount(String code) {
        Verification verification = verificationRepository.findByCode(code)
                .orElseThrow(() -> ApiException.create(HttpStatus.NOT_FOUND).withMessage(
                        messageUtil.getLocalMessage(Constants.ErrorMessage.VERIFY_CODE_NOT_FOUND_BY_CODE) + code));
        Account account = verification.getUser();
        Instant createdDate = verification.getCreated();
        EVerifyStatus status = null;
        if (verification.getIsUsed()) { // Verification phải chưa được sử dụng
            status = EVerifyStatus.USED;
        } else if (!TimeUtil.isLessThanHourDurationOfNow(createdDate, 24)) { // Thời gian xác thực tối đa là 1 ngày
            status = EVerifyStatus.EXPIRED;
        } else {
            account.setVerified(true);
            verification.setIsUsed(true);
            status = EVerifyStatus.SUCCESS;
        }
        return VerifyResponse.Builder.getBuilder().withMessage(status.getMessage()).withStatus(status.name()).build()
                .getObject();
    }

    @Override
    public Boolean resendVerifyEmail() {
        Account currentAccount = SecurityUtil.getCurrentUser();
        if (currentAccount.isVerified()) {
            throw ApiException.create(HttpStatus.FORBIDDEN)
                    .withMessage(messageUtil.getLocalMessage(Constants.ErrorMessage.VERIFIED_ACCOUNT));
        } else {
            emailUtil.sendVerifyEmailTo(currentAccount);
        }
        return true;
    }

    @Override
    public LocalUser processUserRegistrationOAuth2(String registrationId, Map<String, Object> attributes,
                                                   OidcIdToken idToken, OidcUserInfo userInfo) throws IOException {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
        String email = (String) attributes.get("email");
        if (StringUtils.isEmpty(oAuth2UserInfo.getName())) {
            throw ApiException.create(HttpStatus.FORBIDDEN).withMessage(
                    messageUtil.getLocalMessage(Constants.ErrorMessage.NAME_NOT_FOUND_FROM_OAUTH2_PROVIDER));
        } else if (StringUtils.isEmpty(email)) {
            throw ApiException.create(HttpStatus.FORBIDDEN).withMessage(
                    messageUtil.getLocalMessage(Constants.ErrorMessage.EMAIL_NOT_FOUND_FROM_OAUTH2_PROVIDER));
        }
        SignUpRequest userDetails = toUserRegistrationObject(registrationId, oAuth2UserInfo);
        Account account = getAccountByEmail(email);
        if (account != null) {
            /**
             * if (!user.getProvider().equals(SocialProvider.GOOGLE)) {
             * throw
             * ApiException.create(HttpStatus.FORBIDDEN).withMessage(String.format(messageUtil
             * .getLocalMessage(INCORRECT_PROVIDER_LOGIN), user.getProvider(),
             * user.getProvider()));
             * }
             * user = updateExistingUser(user, oAuth2UserInfo);
             *
             */
        } else {
            account = registerNewUser(userDetails);
        }
        return LocalUser.create(account, attributes, idToken, userInfo);
    }

    /**
     * private Account updateExistingUser(Account existingAccount, OAuth2UserInfo
     * oAuth2UserInfo) {
     * existingAccount.setEmail(oAuth2UserInfo.getEmail());
     * return userRepository.save(existingAccount);
     * }
     */

    private SignUpRequest toUserRegistrationObject(String registrationId, OAuth2UserInfo oAuth2UserInfo) {
        return SignUpRequest.getBuilder().addProviderUserID(oAuth2UserInfo.getId())
                .addDisplayName(oAuth2UserInfo.getName()).addEmail(oAuth2UserInfo.getEmail())
                .addSocialProvider(GeneralUtils.toSocialProvider(registrationId)).addPassword("changeit").build();
    }

    private Account registerNewUser(final SignUpRequest signUpRequest) {
        if (signUpRequest.getUserID() != null && accountRepository.existsById(signUpRequest.getUserID())) {
            throw ApiException.create(HttpStatus.NOT_FOUND).withMessage(
                    messageUtil.getLocalMessage(Constants.ErrorMessage.REGISTERED_USER_ID) + signUpRequest.getUserID());
        } else if (accountRepository.existsByEmail(signUpRequest.getEmail())) {
            throw ApiException.create(HttpStatus.NOT_FOUND).withMessage(
                    messageUtil.getLocalMessage(Constants.ErrorMessage.REGISTERED_EMAIL) + signUpRequest.getEmail());
        }
        Account account = buildStudentUser(signUpRequest);
        account = accountRepository.save(account);
        accountRepository.flush();
        return account;
    }

    private Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email).orElse(null);
    }

    private Account buildStudentUser(final SignUpRequest signUpRequest) {
        Account Account = new Account();
        Account.setEmail(signUpRequest.getEmail());
        Account.setPassword(encoder.encode(signUpRequest.getPassword()));
        Account.setProvider(signUpRequest.getSocialProvider());
        Account.setStatus(true);
        Account.setVerified(true);
        return Account;
    }

    @Override
    public ApiPage<UserResponse> getAccountsWithFilter(AccountFilterRequest request, EAccountRole role,
                                                       Pageable pageable) {
        AccountSpecificationBuilder builder = AccountSpecificationBuilder.specificationBuilder();
        if (!request.getEmail().isEmpty()) {
            builder.searchByEmail(request.getEmail());
        } else if (!request.getFullName().isEmpty()) {
            builder.searchByName(request.getFullName());
        } else if (!request.getPhone().isEmpty()) {
            builder.searchByPhone(request.getPhone());
        }
        Specification<Account> accountSpecification = builder.build();
        Page<Account> result = accountRepository.findAll(accountSpecification, pageable);
        return PageUtil.convert(result.map(ConvertUtil::convertUsertoUserResponse));
    }

    @Override
    public UserResponse updateAccountProfile(UpdateAccountRequest request) throws IOException {
        Account currentUser = SecurityUtil.getCurrentUser();
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            currentUser.setFullName(request.getFullName());
        }
        if (request.getGender() != null) {
            currentUser.setGender(request.getGender());
        }
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            currentUser.setAddress(request.getAddress());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            currentUser.setPhone(request.getPhone());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            MultipartFile file = request.getImage();
            String name = request.getFullName() + "_" + Instant.now().toString();
            ObjectWriteResponse objectWriteResponse = minioAdapter.uploadFile(name, file.getContentType(), file.getInputStream(), file.getSize());
            String imageURL = MiniIOUtil.buildUrl(minioUrl, objectWriteResponse);
            currentUser.setImageProfile(imageURL);
        }

        accountRepository.save(currentUser);
        return ConvertUtil.convertUsertoUserResponse(currentUser);
    }
}
