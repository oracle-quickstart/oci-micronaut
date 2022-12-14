package assets.storage;

import io.micronaut.objectstorage.InputStreamMapper;
import io.micronaut.objectstorage.oraclecloud.OracleCloudStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Set;

@Singleton
public class OciAssetUploadHandler extends AssetUploadHandler {

    private final OracleCloudStorageOperations objectStorage;

    public OciAssetUploadHandler(InputStreamMapper inputStreamMapper, OracleCloudStorageOperations objectStorage) {
        super(inputStreamMapper);
        this.objectStorage = objectStorage;
    }

    @Override
    Set<String> listAssets() {
        return objectStorage.listObjects();
    }

    @Override
    void uploadAsset(UploadRequest uploadRequest) {
        objectStorage.upload(uploadRequest, builder -> {
            builder.opcMeta(Map.of("project", "MuShop"));
        });
    }

    @Override
    void deleteAsset(String assetKey) {
        objectStorage.delete(assetKey);
    }

}
