package spring.project.base.service;

import spring.project.base.dto.request.JwtResponse;
import spring.project.base.dto.request.LoginRequest;

public interface IAuthService {
    JwtResponse userLogin(LoginRequest loginRequest);
}