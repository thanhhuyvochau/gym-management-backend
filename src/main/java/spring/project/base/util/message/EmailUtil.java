package spring.project.base.util.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import spring.project.base.common.ApiException;
import spring.project.base.entity.Account;
import spring.project.base.entity.Verification;
import spring.project.base.repository.VerificationRepository;
import spring.project.base.util.formater.StringUtil;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;


@Component
@Transactional
public class EmailUtil {
    @Autowired
    @Qualifier("verifyAccountTemplate")
    private String verifyAccountTemplate;
    @Autowired
    @Qualifier("resetPasswordTemplate")
    private String resetPasswordTemplate;
    @Autowired
    @Qualifier("otpTemplate")
    private String otpTemplate;
    private final JavaMailSender mailSender;
    private final VerificationRepository verificationRepository;
    private final String SEND_FROM = "noreply@gymstamina.gmail";
    @Value("${host.url}")
    private String hostURL;
    @Value("${forgot-password.url}")
    private String returnUrl;
    private final MessageUtil messageUtil;
    private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);

    public EmailUtil(JavaMailSender mailSender, VerificationRepository verificationRepository,
                     MessageUtil messageUtil) {
        this.mailSender = mailSender;
        this.verificationRepository = verificationRepository;
        this.messageUtil = messageUtil;
    }

    public void sendOtpForChangePassWord(Account account) {
        try {
            String subject = "Mã OTP";
            String otpCode;
            Optional<Verification> existCodeInstant;
            do {
                otpCode = StringUtil.generateOTP();
                existCodeInstant = verificationRepository.findByCodeAndIsUsed(otpCode, false);
            } while (existCodeInstant.isPresent());

            Verification.Builder builder = Verification.Builder.getBuilder();
            Verification verification = builder.withCode(otpCode).withUser(account).build().getObject();
            String replace = otpTemplate.replace("%s", otpCode);
            sendHtmlEmail(replace, account.getEmail(), SEND_FROM, subject);
            verificationRepository.save(verification);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw ApiException.create(HttpStatus.INTERNAL_SERVER_ERROR).withMessage("Gửi mail xác thực thất bại!");
        }
    }

    public void sendVerifyEmailTo(Account account) {
        try {
            String subject = "Xác thực tài khoản của bạn";
            String verifyCode = String.valueOf(UUID.randomUUID());
            String activeLink = hostURL + verifyCode;
            Verification.Builder builder = Verification.Builder.getBuilder();
            Verification verification = builder.withCode(verifyCode).withUser(account).build().getObject();
            sendHtmlEmail(String.format(verifyAccountTemplate, activeLink), account.getEmail(), SEND_FROM, subject);
            verificationRepository.save(verification);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw ApiException.create(HttpStatus.INTERNAL_SERVER_ERROR).withMessage("Gửi mail xác thực thất bại!");
        }
    }

    public boolean sendTextPlainEmail(String content, String to, String from, String subject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw ApiException.create(HttpStatus.INTERNAL_SERVER_ERROR).withMessage(messageUtil.getLocalMessage("Your Sending Email Message Exception"));
        }
    }

    public boolean sendHtmlEmail(String content, String to, String from, String subject) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setTo(to);
            boolean html = true;
            helper.setText(content, html);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw ApiException.create(HttpStatus.INTERNAL_SERVER_ERROR).withMessage("Your Sending Email Message Exception");
        }
    }
}
