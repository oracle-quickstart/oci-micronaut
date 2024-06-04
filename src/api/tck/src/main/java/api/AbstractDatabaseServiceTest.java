// package api;

// import io.micronaut.core.annotation.NonNull;
// import io.micronaut.http.BasicAuth;
// import io.micronaut.http.HttpResponse;
// import io.micronaut.http.annotation.Post;
// import io.micronaut.http.client.annotation.Client;
// import io.micronaut.test.support.TestPropertyProvider;

// import org.junit.jupiter.api.AfterAll;
// import org.testcontainers.containers.GenericContainer;
// import org.testcontainers.containers.MongoDBContainer;
// import org.testcontainers.containers.Network;
// import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
// import org.testcontainers.utility.DockerImageName;

// import java.util.HashMap;
// import java.util.Map;

// abstract class AbstractDatabaseServiceTest implements TestPropertyProvider {

//     static MongoDBContainer mongoDBContainer;
//     static GenericContainer<?> natsContainer;
//     static GenericContainer<?> serviceContainer;

//     protected static DockerImageServiceType defaultDockerImageServiceType = DockerImageServiceType.GRAALVM;

//     @AfterAll
//     static void cleanup() {
//         serviceContainer.stop();
//         if (mongoDBContainer != null) {
//             mongoDBContainer.stop();
//         }
//         if (natsContainer != null) {
//             natsContainer.stop();
//         }
//     }

//     @NonNull
//     protected Map<String, String> getProperties(boolean useMongoDB, boolean useNats) {
//         if (useMongoDB) {
//             startMongoDBContainer();
//         }
//         if (useNats) {
//             startNatsContainer();
//         }
//         startServiceContainer(useMongoDB, useNats);
//         return Map.of(
//                 "micronaut.http.services.mushop-" + getServiceId() + ".url", "http://" + serviceContainer.getHost() + ":" + serviceContainer.getFirstMappedPort()
//         );
//     }

//     private void startMongoDBContainer() {
//         mongoDBContainer = new MongoDBContainer("mongo:4.0.10")
//                 .withExposedPorts(27017)
//                 .withNetwork(Network.SHARED)
//                 .withNetworkAliases("mongodb-local");
//         mongoDBContainer.start();
//     }

//     private void startNatsContainer() {
//         natsContainer = new GenericContainer<>("nats:latest")
//                 .withExposedPorts(4222)
//                 .withNetwork(Network.SHARED)
//                 .withNetworkAliases("nats-local")
//                 .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Server is ready.*"));
//         natsContainer.start();
//     }

//     private void startServiceContainer(boolean useMongoDB, boolean useNats) {
//         Map<String, String> env = new HashMap<>();
//         if (useMongoDB) {
//             env.put("MONGODB_URI", "mongodb://mongodb-local:27017/test");
//         }
//         if (useNats) {
//             env.put("NATS_ADDRESSES", "nats://nats-local:4222");
//         }
//         serviceContainer = new GenericContainer<>(composeServiceDockerImage())
//                 .withNetwork(Network.SHARED)
//                 .withExposedPorts(getServiceExposedPort())
//                 .withEnv(env)
//                 .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Startup completed.*"));
//         serviceContainer.start();
//     }

//     protected DockerImageName composeServiceDockerImage() {
//         return DockerImageName.parse("phx.ocir.io/oraclelabs/micronaut-showcase/mushop/" +
//                 getServiceId() + "-app-" + defaultDockerImageServiceType.name().toLowerCase() + ":" + getServiceVersion());
//     }

//     protected int getServiceExposedPort() {
//         return 8080;
//     }

//     protected abstract String getServiceVersion();

//     protected abstract String getServiceId();

//     @Client("/api")
//     interface LoginClient {
//         @Post("/login")
//         HttpResponse<?> login(BasicAuth basicAuth);
//     }

//     enum DockerImageServiceType {
//         GRAALVM,
//         NATIVE
//     }
// }
