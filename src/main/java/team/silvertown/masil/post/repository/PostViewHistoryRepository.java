package team.silvertown.masil.post.repository;

import org.springframework.data.repository.CrudRepository;
import team.silvertown.masil.post.domain.PostViewHistory;

public interface PostViewHistoryRepository extends CrudRepository<PostViewHistory, String> {

}
