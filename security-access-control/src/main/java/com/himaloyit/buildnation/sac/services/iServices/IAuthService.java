package com.himaloyit.buildnation.sac.services.iServices;

import com.himaloyit.buildnation.sac.domain.dto.UserDTO;
import com.himaloyit.buildnation.sac.domain.model.AuthResponse;
import com.himaloyit.buildnation.sac.domain.model.LoginRequest;
import com.himaloyit.buildnation.sac.domain.model.RegisterRequest;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    void logout(String accessToken, String refreshToken);

    UserDTO validateToken(String accessToken);

    UserDTO getCurrentUser(String email);
}
