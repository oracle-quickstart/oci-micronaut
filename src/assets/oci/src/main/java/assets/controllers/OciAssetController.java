package assets.controllers;

import com.oracle.bmc.auth.RegionProvider;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.annotation.Controller;
import io.micronaut.objectstorage.oraclecloud.OracleCloudStorageConfiguration;

@Controller
@Replaces(AssetController.class)
public class OciAssetController extends AssetController {

    private static final String PRODUCT_IMAGE_PATH_FORMAT = "https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s";

    private final String productImagePath;

    public OciAssetController(OracleCloudStorageConfiguration configuration, RegionProvider regionProvider) {
        this.productImagePath = String.format(PRODUCT_IMAGE_PATH_FORMAT,
                regionProvider.getRegion().getRegionId(),
                configuration.getNamespace(),
                configuration.getBucket(),
                "image%2Fproduct%2F");
    }

    @Override
    protected String getProductImagePath() {
        return productImagePath;
    }
}
