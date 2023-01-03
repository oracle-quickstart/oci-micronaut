package assets.controllers;

import assets.storage.OciObjectStorageHandler;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.annotation.Controller;

@Controller
@Replaces(AssetController.class)
public class OciAssetController extends AssetController {

    private final OciObjectStorageHandler objectStorageHandler;

    public OciAssetController(OciObjectStorageHandler objectStorageHandler) {
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
