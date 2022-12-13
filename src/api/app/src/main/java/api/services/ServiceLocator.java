package api.services;

import io.micronaut.http.uri.UriTemplate;
import reactor.core.publisher.Mono;

/**
 * The service locator.
 */
public interface ServiceLocator {

    String CARTS = "mushop-carts";
    String USER = "mushop-user";
    String ORDERS = "mushop-orders";
    String CATALOGUE = "mushop-catalogue";
    String EVENTS = "mushop-events";
    String NEWSLETTER = "mushop-newsletter";
    String ASSETS = "mushop-assets";

    Mono<UriTemplate> getCartsURL();

    Mono<UriTemplate> getUsersURL();
}
