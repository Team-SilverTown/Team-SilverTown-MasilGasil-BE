package team.silvertown.masil.masil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.masil.domain.Masil;

public interface MasilRepository extends JpaRepository<Masil, Long>, MasilQueryRepository {

}
