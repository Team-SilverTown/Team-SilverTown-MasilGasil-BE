package team.silvertown.masil.user.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAuthority;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {

    List<UserAuthority> findByUser(User user);
    @Query(value = "select authority from UserAuthority authority where authority.user.id = :userId")
    List<UserAuthority> findAllByUserId(Long userId);

}
