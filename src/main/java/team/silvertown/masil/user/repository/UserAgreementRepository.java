package team.silvertown.masil.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAgreement;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {

    boolean existsByUser(User user);

}
