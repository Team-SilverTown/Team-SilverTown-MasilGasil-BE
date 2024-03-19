package team.silvertown.masil.auth.service;

import java.io.IOException;
import team.silvertown.masil.user.dto.OAuthResponse;

public interface OAuthService {

    OAuthResponse getUserInfo(String kakaoToken) throws IOException;

}
