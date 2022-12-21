package assets.controllers;

import io.micronaut.objectstorage.oraclecloud.OracleCloudStorageOperations;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MicronautTest
public class OciAssetControllerTest extends AbstractAssetControllerTest {

    @Inject
    private OracleCloudStorageOperations objectStorage;

    @Test
    void testGetAssetLocation() {
        AssetLocationDTO assetLocation = getAssetLocation();
        assertEquals("https://objectstorage.us-phoenix-1.oraclecloud.com/n/test-namespace/b/test-bucket/o/image%2Fproduct%2F", assetLocation.getProductImagePath());
    }

    @Test
    void testDeleteAssets() {
        when(objectStorage.listObjects()).thenReturn(new HashSet<>(Arrays.asList("asset1", "asset2")));
        deleteAssets();
        verify(objectStorage).delete("asset1");
        verify(objectStorage).delete("asset2");
    }

    @MockBean(OracleCloudStorageOperations.class)
    OracleCloudStorageOperations objectStorage() {
        return mock(OracleCloudStorageOperations.class);
    }
}
