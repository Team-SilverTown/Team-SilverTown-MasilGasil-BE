package team.silvertown.masil.masil.dto;

import lombok.Getter;
import team.silvertown.masil.common.map.Address;

@Getter
public class MasilDailyDetailDto {

    private final long id;
    private final Address address;
    private final String content;
    private final String thumbnailUrl;
    private final int distance;
    private final int totalTime;
    private final int calories;

    public MasilDailyDetailDto(
        Long id,
        Address address,
        String content,
        String thumbnailUrl,
        Integer distance,
        Integer totalTime,
        Integer calories
    ) {
        this.id = id;
        this.address = address;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.distance = distance;
        this.totalTime = totalTime;
        this.calories = calories;
    }

}
