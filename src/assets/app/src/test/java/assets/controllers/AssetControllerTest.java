package assets.controllers;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@MicronautTest
public class AssetControllerTest extends AbstractAssetControllerTest {

    @Test
    void testGetAssetLocation() {
        AssetLocationDTO assetLocation = getAssetLocation();
        assertEquals("/assets/image/product/", assetLocation.getProductImagePath());
    }

    @Test
    void testDeleteAssets() {
        try {
            deleteAssets();
            fail();
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED, e.getStatus());
            assertEquals("Method Not Allowed", e.getMessage());
        }
    }
}
