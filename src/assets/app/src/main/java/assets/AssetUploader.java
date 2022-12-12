package assets;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.objectstorage.ObjectStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.exceptions.ApplicationStartupException;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

abstract class AssetUploader<I, O, D> {

    private final ObjectStorageOperations<I, O, D> objectStorage;

    public AssetUploader(ObjectStorageOperations<I, O, D> objectStorage) {
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
                objectStorage.upload(objectStorageUpload, builder -> {
                    builder.acl(ObjectCannedACL.PUBLIC_READ);
                });
            });
        } catch (Exception e) {
            throw new ApplicationStartupException("Failed to upload assets", e);
        }
    }
}
