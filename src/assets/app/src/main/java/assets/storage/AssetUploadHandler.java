package assets.storage;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.objectstorage.InputStreamMapper;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.exceptions.ApplicationStartupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Set;

abstract class AssetUploadHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AssetUploadHandler.class);

    private static final String MANIFEST_FILE = "assets/manifest.txt";

    private final InputStreamMapper inputStreamMapper;

    protected AssetUploadHandler(InputStreamMapper inputStreamMapper) {
        this.inputStreamMapper = inputStreamMapper;
    }

    @EventListener
    void uploadAssets(StartupEvent startupEvent) {
        Set<String> uploadedAssets = listAssets();

        ResourceResolver resourceResolver = new ResourceResolver();
        Optional<InputStream> manifestInputStream = resourceResolver.getResourceAsStream("classpath:" + MANIFEST_FILE);
        if (manifestInputStream.isEmpty()) {
            throw new ApplicationStartupException(MANIFEST_FILE + " not found on the classpath");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(manifestInputStream.get()))) {
            String assetKey;
            while ((assetKey = br.readLine()) != null) {
                if (uploadedAssets.remove(assetKey)) {
                    LOG.info("Skipped already uploaded: {}", assetKey);
                } else {
                    byte[] assetBytes = loadLocalAsset(resourceResolver, "assets/" + assetKey);
                    uploadAsset(UploadRequest.fromBytes(assetBytes, assetKey));
                    LOG.info("Uploaded: {}", assetKey);
                }
            }
        } catch (Exception e) {
            throw new ApplicationStartupException("Failed to upload assets", e);
        }

        // delete removed assets
        uploadedAssets.forEach(assetKey -> {
            deleteAsset(assetKey);
            LOG.info("Deleted: {}", assetKey);
        });
    }

    public byte[] loadLocalAsset(ResourceResolver resourceResolver, String assetPath) {
        Optional<InputStream> assetInputStream = resourceResolver.getResourceAsStream("classpath:" + assetPath);
        if (assetInputStream.isEmpty()) {
            throw new ApplicationStartupException(assetPath + " not found on the classpath");
        }

        byte[] assetBytes = new byte[0];
        try (InputStream inputStream = assetInputStream.get()) {
            assetBytes = inputStreamMapper.toByteArray(inputStream);
        } catch (IOException e) {
            LOG.warn("Failed to close asset input stream: assetPath={}", assetPath, e);
        }
        return assetBytes;
    }

    abstract Set<String> listAssets();

    abstract void uploadAsset(UploadRequest uploadRequest);

    abstract void deleteAsset(String assetKey);

}
