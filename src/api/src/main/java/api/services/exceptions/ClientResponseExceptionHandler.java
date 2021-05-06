package api.services.exceptions;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class ClientResponseExceptionHandler implements ExceptionHandler<HttpClientResponseException, HttpResponse<?>> {
    private static final Logger LOG = LoggerFactory.getLogger(ClientResponseExceptionHandler.class);
    @Override
    public HttpResponse<?> handle(HttpRequest request, HttpClientResponseException exception) {
        final HttpResponse<?> response = exception.getResponse();
        if (response.status().getCode() < 500) {
            final JsonError error = new JsonError(exception.getMessage());
            error.link(Link.SELF, request.getPath());
            return HttpResponse.status(
                    response.status(),
                    response.reason()
            ).body(error);
        } else {
            if (LOG.isErrorEnabled()) {
                LOG.error("Service responded with error " + exception.getMessage(), exception);
            }
            final JsonError error = new JsonError("API currently unavailable due to error");
            error.link(Link.SELF, request.getPath());
            return HttpResponse.serverError(error);
        }
    }
}
