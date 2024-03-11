package team.silvertown.masil.test;

import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class LocalstackTest {

    protected static final String BUCKET_NAME = UUID.randomUUID()
        .toString();
    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse(
        "localstack/localstack:3.0");

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DOCKER_IMAGE_NAME);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.region.static", localStack::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("spring.cloud.aws.s3.bucket", () -> BUCKET_NAME);
        registry.add("spring.cloud.aws.s3.endpoint", LocalstackTest::getS3Endpoint);
    }

    @BeforeAll
    static void setupContainer() throws IOException, InterruptedException {
        localStack.execInContainer("awslocal", "s3", "mb", "s3://" + BUCKET_NAME);
    }

    protected static String getS3Endpoint() {
        return localStack.getEndpointOverride(Service.S3)
            .toString();
    }

}
