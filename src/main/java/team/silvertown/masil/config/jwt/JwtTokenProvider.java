package team.silvertown.masil.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String USER_ID_CLAIM = "user_id";
    private static final int MILLS = 1000;
    private final long tokenValidityInMilliseconds;
    private final String issuer;
    private final MacAlgorithm algorithm;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.base64Secret());

        this.tokenValidityInMilliseconds = jwtProperties.tokenValidityInSeconds() * MILLS;
        this.issuer = jwtProperties.issuer();
        this.algorithm = Jwts.SIG.HS512;
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser()
            .verifyWith(secretKey)
            .requireIssuer(issuer)
            .build();
    }

    public String createToken(long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiration(validity)
            .claim(USER_ID_CLAIM, userId)
            .signWith(secretKey, algorithm)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        long userId = jwtParser.parseSignedClaims(token)
            .getPayload()
            .get(USER_ID_CLAIM, Long.class);

        return new UsernamePasswordAuthenticationToken(userId, token, Collections.emptyList());
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
            log.debug(TokenMessage.INVALID_SIGNATURE, e);
        } else if (e instanceof ExpiredJwtException) {
            log.debug(TokenMessage.EXPIRED_TOKEN, e);
        } else if (e instanceof UnsupportedJwtException) {
            log.debug(TokenMessage.UNSUPPORTED_TOKEN, e);
        } else {
            log.debug(TokenMessage.UNEXPECTED_TOKEN, e);
        }
    }

}
