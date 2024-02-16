package team.silvertown.masil.common.map;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    @Column(name = "address_depth1", length = 20)
    private String depth1;

    @Column(name = "address_depth2", length = 20)
    private String depth2;

    @Column(name = "address_depth3", length = 20)
    private String depth3;

}
