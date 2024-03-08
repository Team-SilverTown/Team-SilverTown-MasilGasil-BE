package team.silvertown.masil.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.silvertown.masil.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}
