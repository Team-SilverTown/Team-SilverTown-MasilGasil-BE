package team.silvertown.masil.common.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.texture.MasilTexture;

@DisplayNameGeneration(ReplaceUnderscores.class)
class AddressTest {

    String addressDepth1;
    String addressDepth2;
    String addressDepth3;

    @BeforeEach
    void setUp() {
        addressDepth1 = MasilTexture.createAddressDepth1();
        addressDepth2 = MasilTexture.createAddressDepth2();
        addressDepth3 = MasilTexture.createAddressDepth3();
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "땡땡리"})
    void 주소_생성을_성공한다(String addressDepth4) {
        // given

        // when
        Address address = new Address(addressDepth1, addressDepth2, addressDepth3, addressDepth4);

        // then
        assertThat(address).extracting("depth1", "depth2", "depth3", "depth4")
            .containsExactly(addressDepth1, addressDepth2, addressDepth3, addressDepth4);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 지역_Depth_1이_빈_값이면_주소_생성을_실패한다(String blankDepth1) {
        // given

        // when
        ThrowingCallable create = () -> new Address(blankDepth1, addressDepth2, addressDepth3, "");

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MapErrorCode.BLANK_DEPTH1.getMessage());
    }

    @ParameterizedTest
    @NullSource
    void 지역_Depth_2가_Null_값이면_주소_생성을_실패한다(String nullDepth2) {
        // given

        // when
        ThrowingCallable create = () -> new Address(addressDepth1, nullDepth2, addressDepth3, "");

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MapErrorCode.NULL_DEPTH2.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 지역_Depth_3이_빈_값이면_주소_생성을_실패한다(String blankDepth3) {
        // given

        // when
        ThrowingCallable create = () -> new Address(addressDepth1, addressDepth2, blankDepth3, "");

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MapErrorCode.BLANK_DEPTH3.getMessage());
    }

    @ParameterizedTest
    @NullSource
    void 지역_Depth_4이_Null_값이면_주소_생성을_실패한다(String nullDepth4) {
        // given

        // when
        ThrowingCallable create = () -> new Address(addressDepth1, addressDepth2, addressDepth3,
            nullDepth4);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MapErrorCode.NULL_DEPTH4.getMessage());
    }

}
