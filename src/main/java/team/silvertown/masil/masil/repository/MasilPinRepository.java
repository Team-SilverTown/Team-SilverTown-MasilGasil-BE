package team.silvertown.masil.masil.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;

public interface MasilPinRepository extends JpaRepository<MasilPin, Long> {

    @Modifying
    @Query("DELETE FROM MasilPin mp WHERE mp.masil = :masil")
    void deleteAllByMasil(Masil masil);

}
