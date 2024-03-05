package team.silvertown.masil.masil.dto.request;

import java.time.LocalDate;

public record PeriodRequest(
    // TODO: apply date parser
    LocalDate startDate,
    LocalDate endDate
) {

}
