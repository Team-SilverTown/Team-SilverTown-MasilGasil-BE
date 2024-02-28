package team.silvertown.masil.masil.dto.response;

import java.util.List;

public record RecentMasilResponse(List<SimpleMasilResponse> masils, boolean isEmpty) {

}
