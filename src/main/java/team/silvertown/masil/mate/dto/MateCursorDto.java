package team.silvertown.masil.mate.dto;

import team.silvertown.masil.mate.domain.Mate;

public record MateCursorDto(
    Mate mate,
    String cursor
) {

}
