package team.silvertown.masil.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.silvertown.masil.user.dto.KakaoResponse;
import team.silvertown.masil.user.dto.OAuthResponse;
import team.silvertown.masil.user.exception.UserValidator;
import team.silvertown.masil.user.service.restTemplate.RestTemplateService;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoOAuthService implements OAuthService {

    private static final String KAKAO_PROVIDER = "kakao";

    private final ObjectMapper objectMapper;
    private final RestTemplateService restTemplateService;

    public OAuthResponse getUserInfo(String kakaoToken) throws IOException {
        HashMap<String, Object> userInfo = new HashMap<>();

        ResponseEntity<String> accountInfoResponse = restTemplateService.requestKaKaoInfo(
            kakaoToken);

        String json = accountInfoResponse.getBody();
        KakaoResponse kakaoResponse = objectMapper.readValue(json, KakaoResponse.class);
        String id = kakaoResponse.getId();
        UserValidator.validateProvidedId(id);

        userInfo.put("id", id);
        userInfo.put("provider", KAKAO_PROVIDER);

        return OAuthResponse.from(userInfo);
    }

}
