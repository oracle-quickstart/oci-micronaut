package api.services;

import api.services.annotation.MuService;
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
        return Single.just(new Configuration(trackId, false, ""));
    }

    @Schema(title = "Session configuration")
    @Introspected
    static class Configuration {
        String trackId;
        boolean mockMode;
        String staticAssetPrefix;

        public Configuration(String trackId, boolean mockMode, String staticAssetPrefix) {
            this.trackId = trackId;
            this.mockMode = mockMode;
            this.staticAssetPrefix = staticAssetPrefix;
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
    }
}
