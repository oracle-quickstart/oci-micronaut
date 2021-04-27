package newsletter.subscription;

import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.oraclecloud.function.OciFunction;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Map;

@Singleton
public class Function extends OciFunction {
    @Inject
    MailSender mailSender;

    @ReflectiveAccess
    public Map<String, String> handleRequest(String toEmail) throws Exception {
        final String messageID = mailSender.send(
                toEmail,
                "Hello from Mushop",
                "Thanks for confirming your <b>subscription</b>!"
        );

        return Collections.singletonMap("messageId", messageID);
    }
}
