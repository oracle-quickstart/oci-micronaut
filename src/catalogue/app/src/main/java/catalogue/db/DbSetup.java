package catalogue.db;

import catalogue.repositories.ProductRepository;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.io.IOUtils;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.exceptions.ApplicationStartupException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

@Singleton
public class DbSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbSetup.class);

    private static final String SETUP_SCRIPT = "db/catalogue_data.sql";

    private final DataSource dataSource;
    private final ProductRepository repository;
    private final ResourceLoader resourceLoader;
    private final String databaseDriver;

    public DbSetup(DataSource dataSource,
                   ProductRepository repository,
                   ResourceLoader resourceLoader,
                   @Value("${datasources.default.driverClassName:`org.h2.Driver`}") String databaseDriver) {
        this.dataSource = dataSource;
        this.repository = repository;
        this.resourceLoader = resourceLoader;
        this.databaseDriver = databaseDriver;
    }

    @EventListener
    @Transactional
    void onStartupEvent(StartupEvent startupEvent) {
        if (repository.count() > 0) {
            return;
        }

        LOGGER.info("Database driver: {}, going to setup DB: {}", databaseDriver, SETUP_SCRIPT);
        resourceLoader.getResourceAsStream(SETUP_SCRIPT).ifPresent((stream) -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                final String sql = IOUtils.readText(reader);
                Scanner scanner = new Scanner(sql).useDelimiter(";");
                while (scanner.hasNext()) {
                    try (Connection connection = dataSource.getConnection()) {
                        try (PreparedStatement ps = connection.prepareStatement(scanner.next())) {
                            ps.execute();
                        }
                    }
                }
            } catch (Exception e) {
                throw new ApplicationStartupException("Unable to populate database schema: " + e.getMessage(), e);
            }
        });
    }
}
