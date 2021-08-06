package api.services;

import api.services.annotation.MuService;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.session.Session;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@MuService
@Secured(SecurityRule.IS_ANONYMOUS)
public class ConfigService {

    @Value("${configuration.cloudProvider:`OCI`}")
    private CLOUD cloudProvider;

    /**
     * Returns user session configuration.
     *
     * @return The user session configuration
     */
    @Tag(name="user")
    @Get("/config")
    Single<Configuration> getConfig(Session session) {
        String trackId = "";
        if (session != null) {
            trackId = session.getId();
        }
        return Single.just(new Configuration(trackId, false, "", cloudProvider));
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
        private final String staticAssetPrefix;
        private final CLOUD cloudProvider;

        public Configuration(String trackId, boolean mockMode, String staticAssetPrefix, CLOUD cloudProvider) {
            this.trackId = trackId;
            this.mockMode = mockMode;
            this.staticAssetPrefix = staticAssetPrefix;
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
         * Static assets prefix.
         */
        public String getStaticAssetPrefix() {
            return staticAssetPrefix;
        }

        /**
         * Cloud provider on which this MuShop runs
         */
        public CLOUD getCloudProvider() {
            return cloudProvider;
        }
    }
}
