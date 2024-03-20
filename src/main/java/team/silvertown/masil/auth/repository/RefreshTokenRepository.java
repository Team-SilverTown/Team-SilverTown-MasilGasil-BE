package team.silvertown.masil.auth.repository;

import org.springframework.data.repository.CrudRepository;
import team.silvertown.masil.auth.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
