package assets.controllers;

import io.micronaut.objectstorage.aws.AwsS3Operations;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MicronautTest
public class AwsAssetControllerTest extends AbstractAssetControllerTest {

    @Inject
    private AwsS3Operations objectStorage;

    @Test
    void testGetAssetLocation() {
        AssetLocationDTO assetLocation = getAssetLocation();
        assertEquals("https://test-bucket.s3.amazonaws.com/image/product/", assetLocation.getProductImagePath());
    }

    @Test
    void testDeleteAssets() {
        when(objectStorage.listObjects()).thenReturn(new HashSet<>(Arrays.asList("asset1", "asset2")));
        deleteAssets();
        verify(objectStorage).delete("asset1");
        verify(objectStorage).delete("asset2");
    }

    @MockBean(AwsS3Operations.class)
    AwsS3Operations objectStorage() {
        return mock(AwsS3Operations.class);
    }
}
