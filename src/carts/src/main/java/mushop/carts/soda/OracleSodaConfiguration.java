package mushop.carts.soda;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.core.convert.format.MapFormat;
import io.micronaut.core.naming.conventions.StringConvention;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

@EachProperty(value = OracleSodaConfiguration.PREFIX, primary = "default")
public class OracleSodaConfiguration {

    public static final String PREFIX = "datasources";

    private SodaConfiguration soda;

    public SodaConfiguration getSoda() {
        return soda;
    }

    public void setSoda(SodaConfiguration soda) {
        this.soda = soda;
    }

    @ConfigurationProperties("soda")
    public static class SodaConfiguration {
        private Properties properties = new Properties();
        private List<String> createCollections = Collections.emptyList();
        private boolean createSodaUser = false;

        /**
         * @return The SODA properties
         */
        public Properties getProperties() {
            return properties;
        }

        public boolean isCreateSodaUser() {
            return createSodaUser;
        }

        public List<String> getCreateCollections() {
            return createCollections;
        }

        /**
         * Sets the collections to create on startup
         * @param createCollections The collections
         */
        public void setCreateCollections(List<String> createCollections) {
            this.createCollections = createCollections;
        }

        /**
         * Sets whether to create the default SODA user.
         * @param createSodaUser True if the SODA user should be created
         */
        public void setCreateSodaUser(boolean createSodaUser) {
            this.createSodaUser = createSodaUser;
        }

        /**
         * Sets any additional SODA properties.
         * @param properties The soda properties
         */
        public void setProperties(
                @MapFormat(keyFormat = StringConvention.RAW, transformation = MapFormat.MapTransformation.FLAT)
                Properties properties) {
            if (properties != null) {
                this.properties = properties;
            }
        }

    }
}
