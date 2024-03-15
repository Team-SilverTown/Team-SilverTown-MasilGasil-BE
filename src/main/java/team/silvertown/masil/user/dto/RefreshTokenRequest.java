package team.silvertown.masil.user.dto;

public record RefreshTokenRequest (
    String expiredToken,
    String refreshToken
){

}
