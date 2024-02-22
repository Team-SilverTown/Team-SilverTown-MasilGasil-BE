package team.silvertown.masil.user.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAuthority;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
    List<UserAuthority> findByUser(User user);
}
