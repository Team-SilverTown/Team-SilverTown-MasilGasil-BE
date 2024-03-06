package team.silvertown.masil.mate.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;
import team.silvertown.masil.mate.dto.request.CreateRequest;
import team.silvertown.masil.mate.dto.response.CreateResponse;
import team.silvertown.masil.mate.repository.MateParticipantRepository;
import team.silvertown.masil.mate.repository.MateRepository;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.repository.PostRepository;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MasilTexture;
import team.silvertown.masil.texture.MateTexture;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.repository.UserRepository;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class MateServiceTest {

    @Autowired
    MateService mateService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MateRepository mateRepository;

    @Autowired
    MateParticipantRepository mateParticipantRepository;

    User author;
    Post post;
    String depth1;
    String depth2;
    String depth3;
    String title;
    String content;
    KakaoPoint gatheringPlacePoint;
    String gatheringPlaceDetail;
    OffsetDateTime gatheringAt;
    Integer capacity;

    @BeforeEach
    void setUp() {
        author = userRepository.save(UserTexture.createValidUser());
        // TODO: apply post texture
        post = postRepository.save(Post.builder()
            .build());
        depth1 = MasilTexture.createAddressDepth1();
        depth2 = MasilTexture.createAddressDepth2();
        depth3 = MasilTexture.createAddressDepth3();
        title = MateTexture.getRandomSentenceWithMax(30);
        content = MateTexture.getRandomSentenceWithMax(1000);
        gatheringPlacePoint = MapTexture.createKakaoPoint();
        gatheringPlaceDetail = MateTexture.getRandomSentenceWithMax(50);
        gatheringAt = MateTexture.getFutureDateTime();
        capacity = MateTexture.getRandomInt(1, 10);
    }

    @Test
    void 메이트_모집_생성을_성공한다() {
        // given
        CreateRequest request = new CreateRequest(post.getId(), depth1, depth2, depth3, "",
            title, content, gatheringPlacePoint, gatheringPlaceDetail, gatheringAt, capacity);

        // when
        CreateResponse expected = mateService.create(author.getId(), request);

        // then
        Mate actual = mateRepository.findAll()
            .stream()
            .findFirst()
            .orElseThrow();
        MateParticipant actualParticipant = mateParticipantRepository.findAll()
            .stream()
            .findFirst()
            .orElseThrow();

        assertThat(expected.id()).isEqualTo(actual.getId());
        assertThat(actualParticipant)
            .hasFieldOrPropertyWithValue("author", author)
            .hasFieldOrPropertyWithValue("status", ParticipantStatus.ACCEPTED);
    }

}
