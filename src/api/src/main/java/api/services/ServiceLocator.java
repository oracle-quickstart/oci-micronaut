package api.services;

import io.micronaut.http.uri.UriTemplate;
import io.reactivex.Single;

public interface ServiceLocator {

    String CARTS = "mushop-carts";
    String USER = "mushop-user";
    String ORDERS = "mushop-orders";
    String CATALOGUE = "mushop-catalogue";
    String EVENTS = "mushop-events";
    String NEWSLETTER = "mushop-newsletter";

    Single<UriTemplate> getCartsURL();

    Single<UriTemplate> getUsersURL();
}
