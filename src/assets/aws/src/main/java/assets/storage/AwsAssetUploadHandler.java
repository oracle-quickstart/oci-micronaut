package assets.storage;

import io.micronaut.objectstorage.InputStreamMapper;
import io.micronaut.objectstorage.aws.AwsS3Operations;
import io.micronaut.objectstorage.request.UploadRequest;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.util.Set;

@Singleton
public class AwsAssetUploadHandler extends AssetUploadHandler {

    private final AwsS3Operations objectStorage;

    public AwsAssetUploadHandler(InputStreamMapper inputStreamMapper, AwsS3Operations objectStorage) {
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
            builder.acl(ObjectCannedACL.PUBLIC_READ);
        });
    }

    @Override
    void deleteAsset(String assetKey) {
        objectStorage.delete(assetKey);
    }

}
