package team.silvertown.masil.config.jwt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenMessage {

    public static final String INVALID_SIGNATURE = "Invalid JWT signature.";
    public static final String EXPIRED_TOKEN = "Expired JWT token.";
    public static final String UNSUPPORTED_TOKEN = "Unsupported JWT token.";
    public static final String UNEXPECTED_TOKEN = "JWT token compact of handler are invalid.";

}
