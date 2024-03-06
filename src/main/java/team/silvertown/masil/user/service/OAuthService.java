package team.silvertown.masil.user.service;

import team.silvertown.masil.user.dto.OAuthResponse;

public interface OAuthService {

    OAuthResponse oauthResponse(String kakaoToken);

}
