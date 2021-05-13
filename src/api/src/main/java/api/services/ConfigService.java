package api.services;

import api.services.annotation.MuService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.session.Session;
import io.micronaut.session.http.HttpSessionFilter;
import io.reactivex.Single;

import java.util.Map;

@MuService
@Secured(SecurityRule.IS_ANONYMOUS)
public class ConfigService {

    @Get("/config")
    Single<Map<String, Object>> getConfig(Session session) {
        String trackId = "";
        if (session != null) {
            trackId = session.getId();
        }
        return Single.just(Map.of(
                "trackId", trackId,
                "mockMode", false,
                "staticAssetPrefix", ""
        ));
    }
}
