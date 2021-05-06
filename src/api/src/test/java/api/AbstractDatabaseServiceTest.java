package api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.Nonnull;
import java.util.Map;

abstract class AbstractDatabaseServiceTest implements TestPropertyProvider {
    static OracleContainer oracleContainer;

    static GenericContainer<?> serviceContainer;

    @AfterAll
    static void cleanup() {
        oracleContainer.stop();
        serviceContainer.stop();
    }

    @Nonnull
    @Override
    public Map<String, String> getProperties() {
        oracleContainer = new OracleContainer("registry.gitlab.com/micronaut-projects/micronaut-graal-tests/oracle-database:18.4.0-xe")
                .withNetwork(Network.SHARED)
                .withNetworkAliases("oracledb");
        oracleContainer.start();
        serviceContainer = initService();
        serviceContainer.start();
        return Map.of(
                "micronaut.http.services." + getServiceId() + ".url", "http://localhost:" + serviceContainer.getFirstMappedPort()
        );
    }

    protected GenericContainer<?> initService() {
        return new GenericContainer<>(
                DockerImageName.parse("iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/" + getServiceId() + ":" + getServiceVersion())
        ).withExposedPorts(getServiceExposedPort())
                .withNetwork(Network.SHARED)
                .withEnv(Map.of(
                        "DATASOURCES_DEFAULT_URL", "jdbc:oracle:thin:system/oracle@oracledb:1521:xe",
                        "DATASOURCES_DEFAULT_USERNAME", oracleContainer.getUsername(),
                        "DATASOURCES_DEFAULT_PASSWORD", oracleContainer.getPassword()
                ));
    }

    protected int getServiceExposedPort() {
        return 8080;
    }

    protected abstract String getServiceVersion();

    protected abstract String getServiceId();

    @Client("/api")
    interface LoginClient {
        @Post("/login")
        HttpResponse<?> login(String username, String password);
    }
}
