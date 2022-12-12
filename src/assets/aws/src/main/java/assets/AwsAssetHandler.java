package assets;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.objectstorage.aws.AwsS3Operations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.objectstorage.response.UploadResponse;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.exceptions.ApplicationStartupException;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Singleton
public class AwsAssetHandler {

    private static final String ASSETS_DIR_NAME = "mushop-assets";

    private final AwsS3Operations objectStorage;

    public AwsAssetHandler(AwsS3Operations objectStorage) {
        this.objectStorage = objectStorage;
    }

    @EventListener
    void uploadAssets(StartupEvent startupEvent) {
        ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
        Optional<URL> assetsDirUrl = loader.getResources("classpath:" + ASSETS_DIR_NAME).findFirst();
        if (assetsDirUrl.isEmpty()) {
            throw new ApplicationStartupException(ASSETS_DIR_NAME + " directory not found on the classpath");
        }



        try (Stream<Path> assetPaths = Files.walk(Paths.get(assetsDirUrl.get().toURI()))) {
            int assetDirLength = assetsDirUrl.get().toURI().getPath().length();
            assetPaths.filter(Files::isRegularFile).forEach(assetPath -> {
                String prefix = assetPath.getParent().toString().substring(assetDirLength + 1);
                UploadRequest objectStorageUpload = UploadRequest.fromPath(assetPath, prefix);
                UploadResponse<PutObjectResponse> response = objectStorage.upload(objectStorageUpload, builder -> {
                    builder.acl(ObjectCannedACL.PUBLIC_READ);
                });
            });
        } catch (Exception e) {
            throw new ApplicationStartupException("Failed to upload assets", e);
        }
    }

}
