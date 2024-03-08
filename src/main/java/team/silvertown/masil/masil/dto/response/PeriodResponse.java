package team.silvertown.masil.masil.dto.response;

import java.util.List;
import team.silvertown.masil.masil.dto.MasilDailyDto;

public record PeriodResponse(
    int totalDistance,
    int totalCounts,
    int totalCalories,
    List<MasilDailyDto> masils
) {

}
