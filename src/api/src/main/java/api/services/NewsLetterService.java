package api.services;

import api.services.annotation.MuService;
import api.services.annotation.TrackEvent;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Single;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@MuService
@Secured(SecurityRule.IS_ANONYMOUS)
public class NewsLetterService {

    private final NewsLetterClient client;

    public NewsLetterService(NewsLetterClient client) {
        this.client = client;
    }

    @Post("/newsletter")
    @TrackEvent("subscribe:newsletter")
    Single<SubscribeResponse> subscribe(@Email @NotBlank String email) {
        return client.post(email)
                .onErrorResumeNext((throwable -> {
                            final HttpStatus status = getStatus(throwable);
                            return Single.error(new HttpStatusException(status, "Unable to sign up for newsletter. Status code: " + status));
                        })
                );
    }

    private HttpStatus getStatus(Throwable throwable) {
        if (throwable instanceof HttpClientResponseException) {
            return ((HttpClientResponseException) throwable).getStatus();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Client(id = "newsletter")
    interface NewsLetterClient {
        @Post("/subscribe")
        Single<SubscribeResponse> post(String email);
    }

    @Introspected
    static class SubscribeResponse {
        private final String messageId;

        SubscribeResponse(String messageId) {
            this.messageId = messageId;
        }

        public String getMessageId() {
            return messageId;
        }
    }
}
