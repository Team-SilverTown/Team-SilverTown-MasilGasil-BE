package team.silvertown.masil.mate.repository.participant;

import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.mate.domain.MateParticipant;

public interface MateParticipantRepository extends JpaRepository<MateParticipant, Long>,
    MateParticipantQueryRepository {

}
