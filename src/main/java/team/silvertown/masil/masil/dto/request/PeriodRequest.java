package team.silvertown.masil.masil.dto.request;

import java.time.LocalDate;

public record PeriodRequest(
    LocalDate startDate,
    LocalDate endDate
) {

}
