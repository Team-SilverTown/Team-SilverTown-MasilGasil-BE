package team.silvertown.masil.mate.repository.mate;

import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.mate.domain.Mate;

public interface MateRepository extends JpaRepository<Mate, Long>, MateQueryRepository {

}
