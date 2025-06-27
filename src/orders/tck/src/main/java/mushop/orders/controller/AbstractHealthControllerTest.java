package mushop.orders.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
// import mushop.orders.AbstractTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractHealthControllerTest{ 

    @Inject
    @Client("/")
    HttpClient httpClient;



    @Test
    void getHealth_returns200() {
        assertEquals(
                HttpStatus.OK.getCode(),
                httpClient.toBlocking().exchange(HttpRequest.GET("/health")).code()
        );
    }
}
