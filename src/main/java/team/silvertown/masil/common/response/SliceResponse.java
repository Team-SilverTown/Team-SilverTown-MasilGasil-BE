package team.silvertown.masil.common.response;

import java.util.List;
import org.springframework.data.domain.Slice;

public record SliceResponse<T>(
    boolean hasNext,
    List<T> contents
) {

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<T>(slice.hasNext(), slice.getContent());
    }

}
