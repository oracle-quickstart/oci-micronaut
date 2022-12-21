package assets.controllers;

import assets.storage.AwsObjectStorageHandler;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.annotation.Controller;

@Controller
@Replaces(AssetController.class)
public class AwsAssetController extends AssetController {

    private final AwsObjectStorageHandler objectStorageHandler;

    public AwsAssetController(AwsObjectStorageHandler objectStorageHandler) {
        this.objectStorageHandler = objectStorageHandler;
    }

    @Override
    protected String getProductImagePath() {
        return objectStorageHandler.getProductImagePath();
    }

    @Override
    protected void deleteUploadedAssets() {
        objectStorageHandler.deleteAssets();
    }
}
