package assets.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testGetAsset() {
        final HttpResponse<byte[]> response = getImage("MU-US-001.png");
        assertTrue(response.getBody().isPresent());
        assertEquals(MediaType.IMAGE_PNG_TYPE, response.getContentType().get());
    }
}
