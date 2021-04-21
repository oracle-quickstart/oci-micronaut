package mushop.carts.soda;

import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.exceptions.ConfigurationException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.jdbc.BasicJdbcConfiguration;
import io.micronaut.transaction.jdbc.DelegatingDataSource;
import oracle.soda.OracleCollection;
import oracle.soda.OracleDatabase;
import oracle.soda.OracleDocument;
import oracle.soda.OracleException;
import oracle.soda.rdbms.OracleRDBMSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Factory for creating instances of {@link OracleRDBMSClient}
 */
@Factory
public class OracleSodaClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(OracleSodaClientFactory.class);

    /**
     * Creates the {@link OracleRDBMSClient}
     *
     * @param configuration The configuration
     * @return the client
     */
    @EachBean(DataSource.class)
    @Context
    protected OracleRDBMSClient sodaClient(
            DataSource dataSource,
            @Parameter BasicJdbcConfiguration jdbcConfiguration,
            @Parameter @Nullable OracleSodaConfiguration configuration,
            ResourceLoader resourceLoader) {
        OracleSodaConfiguration.SodaConfiguration soda = configuration != null ? configuration.getSoda() : null;
        if (soda != null) {
            final boolean isCreateSodaUser = soda.isCreateSodaUser();
            final List<String> collections = soda.getCreateCollections();
            final Properties properties = soda.getProperties();
            final OracleRDBMSClient client = new OracleRDBMSClient(properties);
            final boolean hasCollectionsToCreate = !collections.isEmpty();
            if (isCreateSodaUser) {
                final String username = jdbcConfiguration.getUsername();
                try (Connection connection = DriverManager.getConnection(
                        jdbcConfiguration.getUrl(),
                        jdbcConfiguration.getUsername(),
                        jdbcConfiguration.getPassword()
                )) {

                    connection.prepareStatement("GRANT SODA_APP TO " + username).execute();

                } catch (SQLException e) {
                    throw new ConfigurationException("Error initializing SODA: " + e.getMessage(), e);
                }
            }
            if (hasCollectionsToCreate) {
                try (Connection connection = DelegatingDataSource.unwrapDataSource(dataSource).getConnection()) {
                    final OracleDatabase db = client.getDatabase(connection);
                    for (String collectionName : collections) {
                        // Create the carts collection if it does not exist
                        LOG.info("Initializing DB connection {}", collectionName);
                        OracleCollection col = db.openCollection(collectionName);
                        if (col == null) {
                            LOG.info("Collection '{}' does not exist, creating...", collectionName);
                            // Create a collection (see src/main/resources/metadata.json)
                            // It is OK if multiple processes try to create the collection at the
                            // same time. The collection will simply be returned by createCollection() if it
                            // already exists.
                            final InputStream metaData = resourceLoader.getResourceAsStream("classpath:soda/" + collectionName + "-metadata.json").orElse(null);
                            if (metaData != null) {
                                try (metaData) {
                                    OracleDocument collMeta = db.createDocumentFrom(metaData);
                                    db.admin().createCollection(collectionName, collMeta);
                                } catch (OracleException | IOException e) {
                                    throw new ConfigurationException("Error creating collection [" + collectionName + "]" + e.getMessage(), e);
                                }
                            } else {
                                try {
                                    db.admin().createCollection(collectionName);
                                } catch (OracleException e) {
                                    throw new ConfigurationException("Error creating collection [" + collectionName + "]" + e.getMessage(), e);
                                }
                            }

                        }
                    }
                } catch (SQLException | OracleException e) {
                    throw new ConfigurationException("Error initializing SODA: " + e.getMessage(), e);
                }
            }

            return client;
        } else {
            return new OracleRDBMSClient();
        }
    }
}
