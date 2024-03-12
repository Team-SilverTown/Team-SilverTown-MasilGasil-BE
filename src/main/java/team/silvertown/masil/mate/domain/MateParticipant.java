package team.silvertown.masil.mate.domain;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.mate.validator.MateValidator;
import team.silvertown.masil.user.domain.User;

@Entity
@Table(name = "mate_participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MateParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_id", referencedColumnName = "id")
    private Mate mate;

    @Column(name = "message", length = 255)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(12)")
    private ParticipantStatus status;

    @Builder
    private MateParticipant(
        User user,
        Mate mate,
        String message,
        String status
    ) {
        MateValidator.notNull(user, MateErrorCode.NULL_USER);
        MateValidator.notNull(mate, MateErrorCode.NULL_MATE);

        this.user = user;
        this.mate = mate;
        this.message = message;
        this.status = StringUtils.isBlank(status) ? ParticipantStatus.REQUESTED
            : ParticipantStatus.get(status);
    }

}