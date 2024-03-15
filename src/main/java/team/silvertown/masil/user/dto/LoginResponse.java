package team.silvertown.masil.user.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {

}
