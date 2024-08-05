package team.silvertown.masil.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostPin;

public interface PostPinRepository extends JpaRepository<PostPin, Long> {

    @Modifying
    @Query("DELETE FROM PostPin pp WHERE pp.post = :post")
    void deleteAllByPost(Post post);

}
