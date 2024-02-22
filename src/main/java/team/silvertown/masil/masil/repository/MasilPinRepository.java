package team.silvertown.masil.masil.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;

public interface MasilPinRepository extends JpaRepository<MasilPin, Long> {

    List<MasilPin> findAllByMasil(Masil masil);

}
