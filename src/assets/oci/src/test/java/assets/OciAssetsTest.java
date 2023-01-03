package assets;

import io.micronaut.objectstorage.oraclecloud.OracleCloudStorageOperations;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

import static org.mockito.Mockito.mock;

@MicronautTest
public class OciAssetsTest extends AbstractAssetsTest {

    @Inject
    private OracleCloudStorageOperations objectStorage;

    @MockBean(OracleCloudStorageOperations.class)
    OracleCloudStorageOperations objectStorage() {
        return mock(OracleCloudStorageOperations.class);
    }

}
