package team.silvertown.masil.texture;

import java.util.Locale;
import net.datafaker.Faker;

public class BaseDomainTexture {

    protected BaseDomainTexture() {
    }

    protected static final Faker faker = new Faker(Locale.KOREA);

    public static long getRandomId() {
        return faker.random()
            .nextLong(Long.MAX_VALUE);
    }

}
