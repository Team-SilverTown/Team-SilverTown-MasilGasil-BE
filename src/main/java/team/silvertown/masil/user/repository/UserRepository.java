package team.silvertown.masil.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByNickname(String nickname);

}
