package team.silvertown.masil.common.response;

import java.util.List;

public record ScrollResponse<T>(
    boolean isEmpty,
    List<T> contents,
    String nextCursor
) {

    public static <T> ScrollResponse<T> from(List<T> contents, String nextCursor) {
        return new ScrollResponse<T>(contents.isEmpty(), contents, nextCursor);
    }

}
