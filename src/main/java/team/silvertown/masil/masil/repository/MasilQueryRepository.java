package team.silvertown.masil.masil.repository;

import java.util.List;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.user.domain.User;

public interface MasilQueryRepository {

    List<Masil> findRecent(User user, Integer size);

}
