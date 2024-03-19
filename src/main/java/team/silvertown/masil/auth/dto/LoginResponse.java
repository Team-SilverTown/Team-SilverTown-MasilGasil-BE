package team.silvertown.masil.auth.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {

}
