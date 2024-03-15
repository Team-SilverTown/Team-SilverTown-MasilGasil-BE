package team.silvertown.masil.user.dto;

import java.util.HashMap;

public record OAuthResponse(
    String provider,
    String providerId
) {

    public static OAuthResponse from(HashMap<String, Object> userInfo) {
        String provider = (String) userInfo.get("provider");
        String providerId = (String) userInfo.get("id");
        return new OAuthResponse(provider, providerId);
    }

}
