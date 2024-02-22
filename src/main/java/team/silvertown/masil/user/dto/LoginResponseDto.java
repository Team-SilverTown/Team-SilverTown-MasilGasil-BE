package team.silvertown.masil.user.dto;

import java.util.List;

public record LoginResponseDto(
    String token,
    List<String> authority
){

}
