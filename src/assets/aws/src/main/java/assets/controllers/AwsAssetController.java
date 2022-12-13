package assets.controllers;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.annotation.Controller;
import io.micronaut.objectstorage.aws.AwsS3Configuration;

@Controller
@Replaces(AssetController.class)
public class AwsAssetController extends AssetController {

    private static final String PRODUCT_IMAGE_PATH_FORMAT = "https://%s.s3.amazonaws.com/%s";

    private final String productImagePath;

    public AwsAssetController(AwsS3Configuration configuration) {
        productImagePath = String.format(PRODUCT_IMAGE_PATH_FORMAT, configuration.getBucket(), PRODUCT_IMAGE_BASE_PATH);
    }

    @Override
    protected String getProductImagePath() {
        return productImagePath;
    }
}
