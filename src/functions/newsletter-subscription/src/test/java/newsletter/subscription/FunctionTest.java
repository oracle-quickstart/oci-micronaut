package newsletter.subscription;

import com.fnproject.fn.testing.FnTestingRule;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Factory
public class FunctionTest {

    @Test
    public void testFunction() {
        FnTestingRule rule = FnTestingRule.createDefault()
                .addSharedClass(TestResult.class);
        rule.givenEvent().withBody("foo@bar.com").enqueue();
        rule.thenRun(Function.class, "handleRequest");
        String result = rule.getOnlyResult().getBodyAsString();
        assertEquals("{\"messageId\":\"1111\"}", result);
        assertEquals("Hello from Mushop", TestResult.lastSubject);
        assertEquals("Thanks for confirming your <b>subscription</b>!", TestResult.lastBody);
        assertEquals("foo@bar.com", TestResult.lastTo);
    }

    @Replaces(JavaMailSender.class)
    @Singleton
    MailSender mailSender() {
        return (to, subject, body) -> {
            TestResult.lastTo = to;
            TestResult.lastSubject = subject;
            TestResult.lastBody = body;
            return "1111";
        };
    }

    public static class TestResult {
        public static String lastTo;
        public static String lastSubject;
        public static String lastBody;
    }
}
