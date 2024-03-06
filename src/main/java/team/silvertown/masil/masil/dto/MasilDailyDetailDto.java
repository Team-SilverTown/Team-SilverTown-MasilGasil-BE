package team.silvertown.masil.masil.dto;

import lombok.Getter;
import team.silvertown.masil.masil.domain.MasilTitle;

@Getter
public class MasilDailyDetailDto {

    private final long id;
    private final String title;
    private final String content;
    private final String thumbnailUrl;
    private final int distance;
    private final int totalTime;

    public MasilDailyDetailDto(
        Long id,
        MasilTitle title,
        String content,
        String thumbnailUrl,
        Integer distance,
        Integer totalTime
    ) {
        this.id = id;
        this.title = title.getTitle();
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.distance = distance;
        this.totalTime = totalTime;
    }

}
