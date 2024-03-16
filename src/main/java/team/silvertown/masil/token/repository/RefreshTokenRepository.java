package team.silvertown.masil.token.repository;

import org.springframework.data.repository.CrudRepository;
import team.silvertown.masil.token.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
