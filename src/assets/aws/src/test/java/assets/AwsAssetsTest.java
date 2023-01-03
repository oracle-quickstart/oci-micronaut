package assets;

import io.micronaut.objectstorage.aws.AwsS3Operations;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

import static org.mockito.Mockito.mock;

@MicronautTest
public class AwsAssetsTest extends AbstractAssetsTest {

    @Inject
    private AwsS3Operations objectStorage;

    @MockBean(AwsS3Operations.class)
    AwsS3Operations objectStorage() {
        return mock(AwsS3Operations.class);
    }
}
