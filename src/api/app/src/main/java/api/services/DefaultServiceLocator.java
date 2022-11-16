package api.services;

import io.micronaut.discovery.exceptions.NoAvailableServiceException;
import io.micronaut.http.client.LoadBalancer;
import io.micronaut.http.client.LoadBalancerResolver;
import io.micronaut.http.uri.UriTemplate;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;


/**
 * The {@link ServiceLocator} used to dynamically construct the {@link UriTemplate}s.
 */
@Singleton
class DefaultServiceLocator implements ServiceLocator {

    private final LoadBalancer userLoadBalancer;
    private final LoadBalancer cartLoadBalancer;

    DefaultServiceLocator(LoadBalancerResolver loadBalancerResolver) {
        userLoadBalancer = loadBalancerResolver.resolve(ServiceLocator.USER)
                .orElseThrow(() -> new NoAvailableServiceException(ServiceLocator.USER));
        cartLoadBalancer = loadBalancerResolver.resolve(ServiceLocator.CARTS)
                .orElseThrow(() -> new NoAvailableServiceException(ServiceLocator.CARTS));
    }

    @Override
    public Mono<UriTemplate> getCartsURL() {
        return toUriTemplate(cartLoadBalancer);
    }

    @Override
    public Mono<UriTemplate> getUsersURL() {
        return toUriTemplate(userLoadBalancer);
    }

    private Mono<UriTemplate> toUriTemplate(LoadBalancer cartLoadBalancer) {
        return Mono.from(cartLoadBalancer.select())
                .map(serviceInstance -> UriTemplate.of(serviceInstance.getURI().toString()));
    }
}
