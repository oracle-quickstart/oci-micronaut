package assets.util;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.RegionProvider;
import io.micronaut.context.annotation.Replaces;
import jakarta.inject.Singleton;

@Replaces(ConfigFileAuthenticationDetailsProvider.class)
@Singleton
public class CustomRegionProvider implements RegionProvider {

    @Override
    public Region getRegion() {
        return Region.US_PHOENIX_1;
    }
}
