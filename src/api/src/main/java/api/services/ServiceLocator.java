package api.services;

import io.micronaut.http.uri.UriTemplate;
import io.reactivex.Single;

public interface ServiceLocator {
    String CARTS = "carts";
    String USER = "user";
    String ORDERS = "orders";
    String CATALOGUE = "catalogue";
    String EVENTS = "events";

    Single<UriTemplate> getCartsURL();

    Single<UriTemplate> getUsersURL();
}
