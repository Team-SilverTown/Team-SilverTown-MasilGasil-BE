package team.silvertown.masil.masil.dto;

import lombok.Getter;

@Getter
public class MasilDailyDetailDto {

    private final long id;
    private final String content;
    private final String thumbnailUrl;
    private final int distance;
    private final int totalTime;
    private final int calories;

    public MasilDailyDetailDto(
        Long id,
        String content,
        String thumbnailUrl,
        Integer distance,
        Integer totalTime,
        Integer calories
    ) {
        this.id = id;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.distance = distance;
        this.totalTime = totalTime;
        this.calories = calories;
    }

}
