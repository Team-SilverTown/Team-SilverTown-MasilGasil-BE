package team.silvertown.masil.common.scroll.dto;

<<<<<<< HEAD
import java.util.Objects;
=======
>>>>>>> 3f6963a (feat: 메이트 모집 목록 조회 요청 응답 dto 구현)
import lombok.Getter;
import team.silvertown.masil.common.scroll.OrderType;
import team.silvertown.masil.common.validator.ScrollValidator;

@Getter
public final class ScrollRequest {

<<<<<<< HEAD
    private static final int DEFAULT_SIZE = 10;

=======
>>>>>>> 3f6963a (feat: 메이트 모집 목록 조회 요청 응답 dto 구현)
    private final OrderType order;
    private final String cursor;
    private final int size;

    public ScrollRequest(
        String order,
        String cursor,
<<<<<<< HEAD
        Integer size
=======
        int size
>>>>>>> 3f6963a (feat: 메이트 모집 목록 조회 요청 응답 dto 구현)
    ) {
        OrderType orderType = OrderType.get(order);

        ScrollValidator.validateCursorFormat(cursor, orderType);

        this.order = orderType;
        this.cursor = cursor;
<<<<<<< HEAD
        this.size = Objects.isNull(size) ? DEFAULT_SIZE : size;
=======
        this.size = size;
>>>>>>> 3f6963a (feat: 메이트 모집 목록 조회 요청 응답 dto 구현)
    }

}
