package team.silvertown.masil.texture;

import java.util.Locale;
import net.datafaker.Faker;

public class BaseDomainTexture {

    protected BaseDomainTexture() {
    }

    protected static final Faker faker = new Faker(Locale.KOREA);

    public static long getRandomId() {
        return getRandomLong(1L, Long.MAX_VALUE);
    }

    public static String getRandomSentenceWithMax(int maxLength) {
        return faker.lorem()
            .maxLengthSentence(maxLength);
    }

    public static String getRandomFixedSentence(int length) {
        return faker.lorem()
            .sentence(length);
    }

    public static int getRandomInt(int min, int max) {
        return faker.random()
            .nextInt(min, max);
    }

    public static int getRandomPositive() {
        return faker.number()
            .positive();
    }

    public static int getRandomNegative() {
        return faker.number()
            .negative();
    }

    public static long getRandomLong(long min, long max) {
        return faker.random()
            .nextLong(min, max);
    }

}
