package team.silvertown.masil.masil.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.masil.validator.MasilValidator;

@Entity
@Table(name = "masil_pins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MasilPin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "masil_id", referencedColumnName = "id")
    private Masil masil;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "point", nullable = false)
    private Point point;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url", length = 1024)
    private String thumbnailUrl;

    @Builder
    private MasilPin(Masil masil, Long userId, Point point, String content, String thumbnailUrl) {
        MasilValidator.notNull(masil, MasilErrorCode.NULL_MASIL);
        MasilValidator.validateUrl(thumbnailUrl);

        this.masil = masil;
        this.userId = userId;
        this.point = point;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
    }

}
