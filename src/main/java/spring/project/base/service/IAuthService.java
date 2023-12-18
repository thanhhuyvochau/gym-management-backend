package spring.project.base.service;

import spring.project.base.entity.request.JwtResponse;
import spring.project.base.entity.request.LoginRequest;

public interface IAuthService {
    JwtResponse userLogin(LoginRequest loginRequest);
}