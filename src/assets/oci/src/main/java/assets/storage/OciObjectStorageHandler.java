package assets.storage;

import com.oracle.bmc.auth.RegionProvider;
import io.micronaut.objectstorage.InputStreamMapper;
import io.micronaut.objectstorage.oraclecloud.OracleCloudStorageConfiguration;
import io.micronaut.objectstorage.oraclecloud.OracleCloudStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.Set;

@Singleton
public class OciObjectStorageHandler extends AbstractObjectStorageHandler {

    private static final String OCI_ASSET_PATH_FORMAT = "https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s";

    private final OracleCloudStorageOperations objectStorage;

    private final String productImagePath;

    public OciObjectStorageHandler(InputStreamMapper inputStreamMapper,
                                   OracleCloudStorageOperations objectStorage,
                                   OracleCloudStorageConfiguration configuration,
                                   RegionProvider regionProvider) {
        super(inputStreamMapper);
        this.objectStorage = objectStorage;
        this.productImagePath = String.format(OCI_ASSET_PATH_FORMAT,
                regionProvider.getRegion().getRegionId(),
                configuration.getNamespace(),
                configuration.getBucket(),
                "image%2Fproduct%2F");
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

    public String getProductImagePath() {
        return productImagePath;
    }

}
