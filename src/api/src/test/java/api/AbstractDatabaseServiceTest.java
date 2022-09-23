package api;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.BasicAuth;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.support.TestPropertyProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractDatabaseServiceTest implements TestPropertyProvider {

    static OracleContainer oracleContainer;
    static MongoDBContainer mongoDBContainer;
    static GenericContainer<?> natsContainer;
    static GenericContainer<?> serviceContainer;

    protected static DockerImageServiceType defaultDockerImageServiceType = DockerImageServiceType.GRAALVM;

    @AfterAll
    static void cleanup() {
        serviceContainer.stop();
        if (oracleContainer != null) {
            oracleContainer.stop();
        }
        if (mongoDBContainer != null) {
            mongoDBContainer.stop();
        }
        if (natsContainer != null) {
            natsContainer.stop();
        }
    }

    @NonNull
    protected Map<String, String> getProperties(boolean useOracleDB, boolean useMongoDB, boolean useNats) {
        if (useOracleDB) {
            startOracleContainer();
        }
        if (useMongoDB) {
            startMongoDBContainer();
        }
        if (useNats) {
            startNatsContainer();
        }
        startServiceContainer(useOracleDB, useMongoDB, useNats);
        return Map.of(
                "micronaut.http.services.mushop-" + getServiceId() + ".url", "http://" + serviceContainer.getHost() + ":" + serviceContainer.getFirstMappedPort()
        );
    }

    private void startOracleContainer() {
        oracleContainer = new OracleContainer("gvenzl/oracle-xe:slim")
                .usingSid()
                .withNetwork(Network.SHARED)
                .withNetworkAliases("oracledb");
        oracleContainer.start();
    }

    private void startMongoDBContainer() {
        mongoDBContainer = new MongoDBContainer("mongo:4.0.10")
                .withExposedPorts(27017)
                .withNetwork(Network.SHARED)
                .withNetworkAliases("mongodb-local");
        mongoDBContainer.start();
    }

    private void startNatsContainer() {
        natsContainer = new GenericContainer<>("nats:latest")
                .withExposedPorts(4222)
                .withNetwork(Network.SHARED)
                .withNetworkAliases("nats-local")
                .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Server is ready.*"));
        natsContainer.start();
    }

    private void startServiceContainer(boolean useOracleDB, boolean useMongoDB, boolean useNats) {
        Map<String, String> env = new HashMap<>();
        if (useOracleDB) {
            env.put("DATASOURCES_DEFAULT_URL", "jdbc:oracle:thin:system/oracle@oracledb:1521:xe");
            env.put("DATASOURCES_DEFAULT_DRIVER_CLASS_NAME", oracleContainer.getDriverClassName());
            env.put("DATASOURCES_DEFAULT_USERNAME", oracleContainer.getUsername());
            env.put("DATASOURCES_DEFAULT_PASSWORD", oracleContainer.getPassword());
        }
        if (useMongoDB) {
            env.put("MONGODB_URI", "mongodb://mongodb-local:27017/test");
        }
        if (useNats) {
            env.put("NATS_ADDRESSES", "nats://nats-local:4222");
        }
        serviceContainer = new GenericContainer<>(composeServiceDockerImage())
                .withNetwork(Network.SHARED)
                .withExposedPorts(getServiceExposedPort())
                .withEnv(env)
                .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Startup completed.*"));
        serviceContainer.start();
    }

    protected DockerImageName composeServiceDockerImage() {
        return DockerImageName.parse("iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/" + getServiceId() + "-" + defaultDockerImageServiceType.name().toLowerCase() + ":" + getServiceVersion());
    }

    protected int getServiceExposedPort() {
        return 8080;
    }

    protected abstract String getServiceVersion();

    protected abstract String getServiceId();

    @Client("/api")
    interface LoginClient {
        @Post("/login")
        HttpResponse<?> login(BasicAuth basicAuth);
    }

    enum DockerImageServiceType {
        GRAALVM,
        OPENJDK,
        NATIVE
    }
}
