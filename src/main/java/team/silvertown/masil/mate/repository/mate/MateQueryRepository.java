package team.silvertown.masil.mate.repository.mate;

import java.util.List;
import java.util.Optional;
import team.silvertown.masil.common.scroll.dto.NormalListRequest;
import team.silvertown.masil.common.scroll.dto.ScrollRequest;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.dto.MateCursorDto;
import team.silvertown.masil.post.domain.Post;

public interface MateQueryRepository {

    Optional<Mate> findDetailById(Long id);

    List<MateCursorDto> findScrollByAddress(NormalListRequest request);

    List<MateCursorDto> findScrollByPost(Post post, ScrollRequest request);

}
