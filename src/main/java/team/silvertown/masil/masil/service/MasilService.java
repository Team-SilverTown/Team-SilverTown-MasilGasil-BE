package team.silvertown.masil.masil.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.ErrorCode;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.common.util.DateTimeConverter;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;
import team.silvertown.masil.masil.dto.MasilDailyDetailDto;
import team.silvertown.masil.masil.dto.MasilDailyDto;
import team.silvertown.masil.masil.dto.request.CreatePinRequest;
import team.silvertown.masil.masil.dto.request.CreateRequest;
import team.silvertown.masil.masil.dto.request.PeriodRequest;
import team.silvertown.masil.masil.dto.response.CreateResponse;
import team.silvertown.masil.masil.dto.response.MasilResponse;
import team.silvertown.masil.masil.dto.response.PeriodResponse;
import team.silvertown.masil.masil.dto.response.PinResponse;
import team.silvertown.masil.masil.dto.response.RecentMasilResponse;
import team.silvertown.masil.masil.dto.response.SimpleMasilResponse;
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
            .orElseThrow(getNotFoundException(MasilErrorCode.USER_NOT_FOUND));
        Masil masil = createMasil(request, user);
        Masil savedMasil = masilRepository.save(masil);

        savePins(request.pins(), savedMasil);

        return new CreateResponse(savedMasil.getId());
    }

    @Transactional(readOnly = true)
    public MasilResponse getById(Long userId, Long id) {
        User user = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(MasilErrorCode.USER_NOT_FOUND));
        Masil masil = masilRepository.findById(id)
            .orElseThrow(getNotFoundException(MasilErrorCode.MASIL_NOT_FOUND));

        MasilValidator.validateMasilOwner(masil, user);

        List<PinResponse> pins = PinResponse.listFrom(masil);

        return MasilResponse.from(masil, pins);
    }

    @Transactional(readOnly = true)
    public RecentMasilResponse getRecent(Long userId, Integer size) {
        User user = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(MasilErrorCode.USER_NOT_FOUND));
        List<SimpleMasilResponse> masils = masilRepository.findRecent(user, size)
            .stream()
            .map(SimpleMasilResponse::from)
            .toList();

        return new RecentMasilResponse(masils, masils.isEmpty());
    }

    @Transactional(readOnly = true)
    public PeriodResponse getInGivenPeriod(Long userId, PeriodRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(MasilErrorCode.USER_NOT_FOUND));
        OffsetDateTime startDateTime = getStartDateTime(request.startDate());
        OffsetDateTime endDateTime = getEndDateTime(request.endDate(), startDateTime);
        List<MasilDailyDto> dailyMasils = masilRepository.findInGivenPeriod(user, startDateTime,
            endDateTime);
        int totalCount = getTotalCount(dailyMasils);
        int totalDistance = getTotalDistance(dailyMasils);
        int totalCalories = getTotalCalories(dailyMasils);

        return new PeriodResponse(totalDistance, totalCount, totalCalories, dailyMasils);
    }

    private Supplier<DataNotFoundException> getNotFoundException(ErrorCode errorCode) {
        return () -> new DataNotFoundException(errorCode);
    }

    private Masil createMasil(CreateRequest request, User user) {
        return Masil.builder()
            .depth1(request.depth1())
            .depth2(request.depth2())
            .depth3(request.depth3())
            .depth4(request.depth4())
            .distance(request.distance())
            .thumbnailUrl(request.thumbnailUrl())
            .content(request.content())
            .path(KakaoPointMapper.mapToLineString(request.path()))
            .postId(request.postId())
            .startedAt(request.startedAt())
            .totalTime(request.totalTime())
            .calories(request.calories())
            .user(user)
            .build();
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

    private OffsetDateTime getStartDateTime(LocalDate date) {
        LocalDate startDate = date;

        if (Objects.isNull(date)) {
            startDate = OffsetDateTime.now(ZoneId.of("Asia/Seoul"))
                .toLocalDate()
                .withDayOfMonth(1);
        }

        return DateTimeConverter.toBeginningOfDay(startDate);
    }

    private OffsetDateTime getEndDateTime(LocalDate date, OffsetDateTime startDateTime) {
        LocalDate endDate = date;

        if (Objects.isNull(date)) {
            YearMonth yearMonth = YearMonth.of(startDateTime.getYear(), startDateTime.getMonth());
            endDate = yearMonth.atEndOfMonth();
        }

        return DateTimeConverter.toEndOfDay(endDate);
    }

    private int getTotalCount(List<MasilDailyDto> dailyMasils) {
        return dailyMasils.stream()
            .mapToInt(dailyMasil -> dailyMasil.masils()
                .size())
            .sum();
    }

    private int getTotalDistance(List<MasilDailyDto> dailyMasils) {
        return dailyMasils.stream()
            .map(this::getDailyDistance)
            .reduce(Integer::sum)
            .orElse(0);
    }

    private int getTotalCalories(List<MasilDailyDto> dailyMasils) {
        return dailyMasils.stream()
            .map(this::getDailyCalories)
            .reduce(Integer::sum)
            .orElse(0);
    }

    private int getDailyCalories(MasilDailyDto dailyMasil) {
        BiFunction<Integer, MasilDailyDetailDto, Integer> accumulator = (accumulated, nextMasil) ->
            accumulated + nextMasil.getCalories();

        return dailyMasil.masils()
            .stream()
            .reduce(0, accumulator, Integer::sum);
    }

    private int getDailyDistance(MasilDailyDto dailyMasil) {
        BiFunction<Integer, MasilDailyDetailDto, Integer> accumulator = (accumulated, nextMasil) ->
            accumulated + nextMasil.getDistance();

        return dailyMasil.masils()
            .stream()
            .reduce(0, accumulator, Integer::sum);
    }

}
