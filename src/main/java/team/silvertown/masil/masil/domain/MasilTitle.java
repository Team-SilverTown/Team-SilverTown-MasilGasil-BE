package team.silvertown.masil.masil.domain;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.silvertown.masil.masil.validator.MasilValidator;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MasilTitle {

    private static final String DEFAULT_TITLE_SUFFIX = " 산책 기록";

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    public MasilTitle(String title) {
        MasilValidator.validateTitle(title);

        this.title = StringUtils.isNotBlank(title) ? title : generateDefaultTitle();
    }

    private String generateDefaultTitle() {
        LocalDate today = OffsetDateTime.now()
            .toLocalDate();

        return today.toString() + DEFAULT_TITLE_SUFFIX;
    }

}
