package team.silvertown.masil.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import io.micrometer.common.util.StringUtils;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.auth.dto.LoginResponse;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String INVALID_SIGNATURE = "Invalid JWT signature.";
    private static final String EXPIRED_TOKEN = "Expired JWT token.";
    private static final String UNSUPPORTED_TOKEN = "Unsupported JWT token.";
    private static final String UNEXPECTED_TOKEN = "JWT token compact of handler are invalid.";
    private static final String NO_AUTHORITY = "No authority included in JWT";
    private static final String USER_ID_CLAIM = "user_id";
    private static final String AUTHORITIES_CLAIM = "authorities";
    private static final String AUTHORITIES_DELIM = " ";
    private static final int MILLS = 1000;

    private final long accessTokenValidityInMilliseconds;
    private final String issuer;
    private final MacAlgorithm algorithm;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.base64Secret());

        this.accessTokenValidityInMilliseconds = jwtProperties.accessTokenValidityInSeconds()
            * MILLS;
        this.issuer = jwtProperties.issuer();
        this.algorithm = Jwts.SIG.HS512;
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser()
            .verifyWith(secretKey)
            .requireIssuer(issuer)
            .build();
    }

    public LoginResponse createToken(long userId, List<Authority> authorities) {
        String accessToken = createAccessToken(userId, authorities);
        String refreshToken = createRefreshToken(userId);

        return new LoginResponse(accessToken, refreshToken);
    }

    public String createAccessToken(long userId, List<Authority> authorities) {
        Date now = new Date();
        Date accessValidity = new Date(now.getTime() + accessTokenValidityInMilliseconds);
        StringJoiner joiner = new StringJoiner(" ");
        authorities.forEach(authority -> joiner.add(authority.getAuthority()));

        return Jwts.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiration(accessValidity)
            .claim(USER_ID_CLAIM, userId)
            .claim(AUTHORITIES_CLAIM, joiner.toString())
            .signWith(secretKey, algorithm)
            .compact();
    }

    private String createRefreshToken(Long userId) {
        return Jwts.builder()
            .issuer(issuer)
            .claim(USER_ID_CLAIM, userId)
            .signWith(secretKey, algorithm)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseSignedClaims(token)
            .getPayload();
        Long userId = claims.get(USER_ID_CLAIM, Long.class);
        String authorityNames = claims.get(AUTHORITIES_CLAIM, String.class);

        if (StringUtils.isBlank(authorityNames)) {
            throw new InsufficientAuthenticationException(NO_AUTHORITY);
        }

        List<Authority> authorities = Arrays.stream(authorityNames.split(AUTHORITIES_DELIM))
            .map(Authority::get)
            .toList();

        return new UsernamePasswordAuthenticationToken(userId, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
        } catch (JwtException | IllegalArgumentException e) {
            loggingException(e);

            return false;
        }

        return true;
    }

    private void loggingException(RuntimeException e) {
        if (e instanceof SecurityException || e instanceof MalformedJwtException) {
            log.debug(INVALID_SIGNATURE, e);
        } else if (e instanceof ExpiredJwtException) {
            log.debug(EXPIRED_TOKEN, e);
        } else if (e instanceof UnsupportedJwtException) {
            log.debug(UNSUPPORTED_TOKEN, e);
        } else {
            log.debug(UNEXPECTED_TOKEN, e);
        }
    }

}
