package assets.storage;

import io.micronaut.objectstorage.InputStreamMapper;
import io.micronaut.objectstorage.aws.AwsS3Configuration;
import io.micronaut.objectstorage.aws.AwsS3Operations;
import io.micronaut.objectstorage.request.UploadRequest;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.util.Set;

@Singleton
public class AwsObjectStorageHandler extends AbstractObjectStorageHandler {

    private static final String AWS_ASSET_PATH_FORMAT = "https://%s.s3.amazonaws.com/%s";

    private final AwsS3Operations objectStorage;

    private final String productImagePath;

    public AwsObjectStorageHandler(InputStreamMapper inputStreamMapper,
                                   AwsS3Operations objectStorage,
                                   AwsS3Configuration configuration) {
        super(inputStreamMapper);
        this.objectStorage = objectStorage;
        productImagePath = String.format(AWS_ASSET_PATH_FORMAT, configuration.getBucket(), "image/product/");
    }

    @Override
    Set<String> listAssets() {
        return objectStorage.listObjects();
    }

    @Override
    void uploadAsset(UploadRequest uploadRequest) {
        objectStorage.upload(uploadRequest, builder -> {
            builder.acl(ObjectCannedACL.PUBLIC_READ);
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
