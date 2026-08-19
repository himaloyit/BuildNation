package com.himaloyit.buildnation.sac.rbac.services.iServices;

import com.himaloyit.buildnation.sac.rbac.domain.dto.PrincipalDTO;
import com.himaloyit.buildnation.sac.rbac.domain.model.AuthResponse;
import com.himaloyit.buildnation.sac.rbac.domain.model.LoginRequest;
import com.himaloyit.buildnation.sac.rbac.domain.model.RegisterRequest;

/*
 * Author: Rajib Kumer Ghosh
 */

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    void logout(String accessToken, String refreshToken);

    PrincipalDTO validateToken(String accessToken);

    PrincipalDTO getCurrentUser(String principalCode);
}
