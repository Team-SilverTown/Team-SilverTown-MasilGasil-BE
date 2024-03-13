package team.silvertown.masil.mate.dto;

import team.silvertown.masil.mate.dto.response.SimpleMateResponse;

public record MateCursorDto(
    SimpleMateResponse mate,
    String cursor
) {

}
