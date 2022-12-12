package assets;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.exceptions.ApplicationStartupException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Singleton
abstract class AssetUploadHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AssetUploadHandler.class);

    private static final String ASSETS_DIR_NAME = "mushop-assets";

    @EventListener
    void uploadAssets(StartupEvent startupEvent) {
        ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
        Optional<URL> assetsDirUrl = loader.getResources("classpath:" + ASSETS_DIR_NAME).findFirst();
        if (assetsDirUrl.isEmpty()) {
            throw new ApplicationStartupException(ASSETS_DIR_NAME + " directory not found on the classpath");
        }

        Path assetsDirPath = Paths.get(assetsDirUrl.get().getPath());

        try (Stream<Path> assetPaths = Files.walk(assetsDirPath)) {
            Set<String> uploadedAssets = listAssets();

            int assetDirLength = assetsDirPath.toString().length();
            assetPaths.filter(Files::isRegularFile).forEach(assetPath -> {
                String prefix = assetPath.getParent().toString().substring(assetDirLength + 1);
                String assetKey = prefix + "/" + assetPath.getFileName();
                if (uploadedAssets.remove(assetKey)) {
                    LOG.info("Skipped already uploaded: {}", assetKey);
                } else {
                    uploadAsset(UploadRequest.fromPath(assetPath, prefix));
                    LOG.info("Uploaded: {}", assetKey);
                }
            });

            // delete removed assets
            uploadedAssets.forEach(assetKey -> {
                deleteAsset(assetKey);
                LOG.info("Deleted: {}", assetKey);
            });
        } catch (Exception e) {
            throw new ApplicationStartupException("Failed to upload assets", e);
        }
    }

    abstract Set<String> listAssets();

    abstract void uploadAsset(UploadRequest uploadRequest);

    abstract void deleteAsset(String assetKey);

}
