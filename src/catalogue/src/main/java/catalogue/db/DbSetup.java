package catalogue.db;

import catalogue.repositories.ProductRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.io.IOUtils;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.exceptions.ApplicationStartupException;

import javax.inject.Singleton;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

@Singleton
public class DbSetup {

    private final DataSource dataSource;
    private final ProductRepository repository;
    private final ResourceLoader resourceLoader;

    public DbSetup(DataSource dataSource,
                   ProductRepository repository,
                   ResourceLoader resourceLoader) {
        this.dataSource = dataSource;
        this.repository = repository;
        this.resourceLoader = resourceLoader;
    }

    @EventListener
    @Transactional
    void init(StartupEvent startupEvent) {
        if (repository.count() > 0) {
            return;
        }

        resourceLoader.getResourceAsStream("db/catalogue.sql").ifPresent((stream) -> {
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
