package team.silvertown.masil.post.repository;

import java.util.List;
import team.silvertown.masil.common.response.ScrollRequest;
import team.silvertown.masil.post.dto.PostCursorDto;
import team.silvertown.masil.post.dto.request.NormalListRequest;
import team.silvertown.masil.user.domain.User;

public interface PostQueryRepository {

    List<PostCursorDto> findScrollByAddress(User user, NormalListRequest request);

    List<PostCursorDto> findScrollByUser(User loginUser, User author, ScrollRequest request);

}
