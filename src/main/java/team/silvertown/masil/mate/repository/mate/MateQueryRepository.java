package team.silvertown.masil.mate.repository.mate;

import java.util.Optional;
import team.silvertown.masil.mate.domain.Mate;

public interface MateQueryRepository {

    Optional<Mate> findDetailById(Long id);

}
