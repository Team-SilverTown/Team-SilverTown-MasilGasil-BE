package team.silvertown.masil.common.map;

public record KakaoPoint(double lat, double lng) {

    public String toRawString() {
        return this.lat + " " + this.lng;
    }

}
