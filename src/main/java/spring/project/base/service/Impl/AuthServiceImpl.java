package spring.project.base.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.project.base.config.security.jwt.JwtUtils;
import spring.project.base.config.security.service.UserDetailsImpl;
import spring.project.base.common.ApiException;
import spring.project.base.dto.request.JwtResponse;
import spring.project.base.dto.request.LoginRequest;
import spring.project.base.repository.RoleRepository;
import spring.project.base.repository.AccountRepository;
import spring.project.base.service.IAuthService;
import spring.project.base.util.message.MessageUtil;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    MessageUtil messageUtil;

    @Override
    public JwtResponse userLogin(LoginRequest loginRequest) {
        Authentication authentication = null;


        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        } catch (Exception e) {
            throw ApiException.create(HttpStatus.BAD_REQUEST)
                    .withMessage("Sai thông tin đăng nhập");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());



        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                roles);

    }

}
