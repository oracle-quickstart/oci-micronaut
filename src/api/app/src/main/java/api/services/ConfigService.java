package api.services;

import api.model.AssetsLocation;
import api.services.annotation.MuService;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.session.Session;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * The MuShop UI config service.
 */
@MuService
@Secured(SecurityRule.IS_ANONYMOUS)
public class ConfigService {

    @Value("${configuration.cloudProvider:`OCI`}")
    private CLOUD cloudProvider;

    private final AssetsClient assetsClient;

    ConfigService(AssetsClient assetsClient) {
        this.assetsClient = assetsClient;
    }

    /**
     * Returns user session configuration.
     *
     * @return The user session configuration
     */
    @Tag(name="user")
    @Get("/config")
    Mono<Configuration> getConfig(Session session) {
        final String trackId = session != null ? session.getId() : "";
        return assetsClient.getAssetsLocation()
                .flatMap(location -> Mono.just(new Configuration(trackId, false, location.getProductImagePath(), cloudProvider)));
    }

    /**
     * Enumerates supported cloud providers based on which the UI in storefront is configured.
     */
    public enum CLOUD {
        OCI,
        AWS
    }

    @Schema(title = "Session configuration")
    @Introspected
    static class Configuration {
        private final String trackId;
        private final boolean mockMode;
        private final String productImagePath;
        private final CLOUD cloudProvider;

        public Configuration(String trackId, boolean mockMode, String productImagePath, CLOUD cloudProvider) {
            this.trackId = trackId;
            this.mockMode = mockMode;
            this.productImagePath = productImagePath;
            this.cloudProvider = cloudProvider;
        }

        /**
         * User session tracking id.
         */
        public String getTrackId() {
            return trackId;
        }

        /**
         * Mock mode flag.
         */
        public boolean isMockMode() {
            return mockMode;
        }

        /**
         * Product image path.
         */
        public String getProductImagePath() {
            return productImagePath;
        }

        /**
         * Cloud provider on which this MuShop runs
         */
        public CLOUD getCloudProvider() {
            return cloudProvider;
        }
    }
}
