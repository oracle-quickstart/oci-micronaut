package assets.controllers;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.annotation.Controller;
import io.micronaut.objectstorage.oraclecloud.OracleCloudStorageConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@Replaces(AssetController.class)
public class OciAssetController extends AssetController {

    private static final String PRODUCT_IMAGE_PATH_FORMAT = "https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s";

    private final String productImagePath;

    public OciAssetController(OracleCloudStorageConfiguration configuration, @Value("${REGION}") String region) {
        String productImageBasePath;
        try {
            productImageBasePath = URLEncoder.encode(PRODUCT_IMAGE_BASE_PATH, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("No available charset: " + e.getMessage());
        }

        this.productImagePath = String.format(PRODUCT_IMAGE_PATH_FORMAT,
                region,
                configuration.getNamespace(),
                configuration.getBucket(),
                productImageBasePath);
    }

    @Override
    protected String getProductImagePath() {
        return productImagePath;
    }
}
