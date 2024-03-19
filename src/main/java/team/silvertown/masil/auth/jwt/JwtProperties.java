package team.silvertown.masil.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtProperties(
    String issuer,
    String base64Secret,
    long accessTokenValidityInSeconds
) {

}
