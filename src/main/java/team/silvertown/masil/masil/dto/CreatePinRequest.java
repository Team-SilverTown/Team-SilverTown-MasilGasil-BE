package team.silvertown.masil.masil.dto;

import team.silvertown.masil.common.map.KakaoPoint;

public record CreatePinRequest(KakaoPoint point, String content, String thumbnailUrl) {

}
