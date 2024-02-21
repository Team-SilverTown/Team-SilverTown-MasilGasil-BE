package team.silvertown.masil.masil.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;
import team.silvertown.masil.masil.dto.CreatePinRequest;
import team.silvertown.masil.masil.dto.CreateRequest;
import team.silvertown.masil.masil.dto.CreateResponse;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.masil.repository.MasilPinRepository;
import team.silvertown.masil.masil.repository.MasilRepository;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MasilService {

    private final MasilRepository masilRepository;
    private final MasilPinRepository masilPinRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateResponse create(Long userId, CreateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(MasilErrorCode.USER_NOT_FOUND));
        Masil masil = createMasil(request, user);
        Masil savedMasil = masilRepository.save(masil);

        savePins(request.pins(), savedMasil);

        return new CreateResponse(savedMasil.getId());
    }

    private Masil createMasil(CreateRequest request, User user) {
        return Masil.builder()
            .depth1(request.depth1())
            .depth2(request.depth2())
            .depth3(request.depth3())
            .depth4(request.depth4())
            .distance(request.distance())
            .thumbnailUrl(request.thumbnailUrl())
            .title(request.title())
            .content(request.content())
            .path(KakaoPointMapper.mapToLineString(request.path()))
            .postId(request.postId())
            .startedAt(request.startedAt())
            .totalTime(request.totalTime())
            .user(user)
            .build();
    }

    private void savePins(List<CreatePinRequest> pins, Masil masil) {
        if (Objects.nonNull(pins) && !pins.isEmpty()) {
            pins.forEach(pin -> savePin(pin, masil));
        }
    }

    private void savePin(CreatePinRequest pin, Masil masil) {
        MasilPin masilPin = createPin(pin, masil);

        masilPinRepository.save(masilPin);
    }

    private MasilPin createPin(CreatePinRequest request, Masil masil) {
        return MasilPin.builder()
            .point(KakaoPointMapper.mapToPoint(request.point()))
            .content(request.content())
            .thumbnailUrl(request.thumbnailUrl())
            .masil(masil)
            .build();
    }

}
