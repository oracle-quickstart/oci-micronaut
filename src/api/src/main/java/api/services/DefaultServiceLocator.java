package api.services;

import io.micronaut.discovery.exceptions.NoAvailableServiceException;
import io.micronaut.http.client.LoadBalancer;
import io.micronaut.http.client.LoadBalancerResolver;
import io.micronaut.http.uri.UriTemplate;
import io.reactivex.Single;

import javax.inject.Singleton;

@Singleton
class DefaultServiceLocator implements ServiceLocator {
    private final LoadBalancer userLoadBalancer;
    private final LoadBalancer cartLoadBalancer;

    DefaultServiceLocator(LoadBalancerResolver loadBalancerResolver) {
        this.userLoadBalancer = loadBalancerResolver.resolve(ServiceLocator.USER)
                .orElseThrow(() -> new NoAvailableServiceException(ServiceLocator.USER));
        this.cartLoadBalancer = loadBalancerResolver.resolve(ServiceLocator.CARTS)
                .orElseThrow(() -> new NoAvailableServiceException(ServiceLocator.CARTS));
    }

    @Override
    public Single<UriTemplate> getCartsURL() {
        return toUriTemplate(cartLoadBalancer);
    }

    @Override
    public Single<UriTemplate> getUsersURL() {
        return toUriTemplate(userLoadBalancer);
    }

    private Single<UriTemplate> toUriTemplate(LoadBalancer cartLoadBalancer) {
        return Single.fromPublisher(cartLoadBalancer.select()).map(serviceInstance -> UriTemplate.of(serviceInstance.getURI().toString()));
    }
}
