package team.silvertown.masil.masil.dto;

import java.util.List;

public record MasilDailyDto(
    String date,
    List<MasilDailyDetailDto> masils
) {

}
