package team.silvertown.masil.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.post.domain.PostPin;

public interface PostPinRepository extends JpaRepository<PostPin, Long> {

}
