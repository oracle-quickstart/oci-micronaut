package mushop.orders.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class HealthControllerTest {

    @Inject
    @Client("/")
    RxHttpClient httpClient;

    @Test
    void getHealth_returns200() {
        assertEquals(HttpStatus.OK.getCode(), httpClient.toBlocking().exchange(HttpRequest.GET("/health")).code());
    }
}
