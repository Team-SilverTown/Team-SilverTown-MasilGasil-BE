package team.silvertown.masil.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.silvertown.masil.post.domain.PostLike;
import team.silvertown.masil.post.domain.PostLikeId;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    @Query("SELECT COUNT(*) FROM PostLike pl WHERE pl.id.postId = :postId AND pl.isLike = true")
    int countByPostId(Long postId);

}
