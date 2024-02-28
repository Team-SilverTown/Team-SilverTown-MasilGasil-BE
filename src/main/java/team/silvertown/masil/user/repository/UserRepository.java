package team.silvertown.masil.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndSocialId(Provider provider, String socialId);

}
