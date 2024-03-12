package team.silvertown.masil.common.map;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.validator.Validator;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Address {

    @Column(name = "address_depth1", length = 20, nullable = false)
    private String depth1;

    @Column(name = "address_depth2", length = 20, nullable = false)
    private String depth2;

    @Column(name = "address_depth3", length = 20, nullable = false)
    private String depth3;

    @Column(name = "address_depth4", length = 20, nullable = false)
    private String depth4;

    public Address(String depth1, String depth2, String depth3, String depth4) {
        Validator.notBlank(depth1, MapErrorCode.BLANK_DEPTH1);
        Validator.notNull(depth2, MapErrorCode.NULL_DEPTH2);
        Validator.notBlank(depth3, MapErrorCode.BLANK_DEPTH3);
        Validator.notNull(depth4, MapErrorCode.NULL_DEPTH4);

        this.depth1 = depth1;
        this.depth2 = depth2;
        this.depth3 = depth3;
        this.depth4 = depth4;
    }

}
