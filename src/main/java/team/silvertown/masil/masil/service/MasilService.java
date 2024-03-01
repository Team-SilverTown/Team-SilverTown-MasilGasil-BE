package team.silvertown.masil.masil.service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.ErrorCode;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;
import team.silvertown.masil.masil.dto.CreatePinRequest;
import team.silvertown.masil.masil.dto.CreateRequest;
import team.silvertown.masil.masil.dto.CreateResponse;
import team.silvertown.masil.masil.dto.MasilResponse;
import team.silvertown.masil.masil.dto.PinResponse;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.masil.repository.MasilPinRepository;
import team.silvertown.masil.masil.repository.MasilRepository;
import team.silvertown.masil.masil.validator.MasilValidator;
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
            .orElseThrow(throwNotFound(MasilErrorCode.USER_NOT_FOUND));
        Masil masil = createMasil(request, user);

        savePins(request.pins(), masil);

        return new CreateResponse(masil.getId());
    }

    @Transactional(readOnly = true)
    public MasilResponse getById(Long userId, Long id) {
        User user = userRepository.findById(userId)
            .orElseThrow(throwNotFound(MasilErrorCode.USER_NOT_FOUND));
        Masil masil = masilRepository.findById(id)
            .orElseThrow(throwNotFound(MasilErrorCode.MASIL_NOT_FOUND));

        MasilValidator.validateMasilOwner(masil, user);

        List<PinResponse> pins = PinResponse.listFrom(masil);

        return MasilResponse.from(masil, pins);
    }

    private Supplier<DataNotFoundException> throwNotFound(ErrorCode errorCode) {
        return () -> new DataNotFoundException(errorCode);
    }

    private Masil createMasil(CreateRequest request, User user) {
        Masil masil = Masil.builder()
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

        return masilRepository.save(masil);
    }

    private void savePins(List<CreatePinRequest> pins, Masil masil) {
        if (Objects.nonNull(pins)) {
            pins.forEach(pin -> savePin(pin, masil));
        }
    }

    private void savePin(CreatePinRequest pin, Masil masil) {
        MasilPin masilPin = createPin(pin, masil);

        masilPinRepository.save(masilPin);
    }

    private MasilPin createPin(CreatePinRequest request, Masil masil) {
        User owner = masil.getUser();

        return MasilPin.builder()
            .userId(owner.getId())
            .point(KakaoPointMapper.mapToPoint(request.point()))
            .content(request.content())
            .thumbnailUrl(request.thumbnailUrl())
            .masil(masil)
            .build();
    }

}
