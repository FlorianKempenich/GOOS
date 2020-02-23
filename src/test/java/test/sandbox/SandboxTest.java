package test.sandbox;

import auctionsniper.sandbox.Greeter;
import auctionsniper.sandbox.MailSender;
import auctionsniper.sandbox.Sandbox;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("sandbox")
class SandboxTest {

    @Mock Greeter greeter;
    @Mock MailSender mailSender;
    @InjectMocks Sandbox sandbox;

    public static Stream<Arguments> addTwoNumbersParams() {
        return Stream.of(
                Arguments.of(1, 1, 2),
                Arguments.of(1, 4, 5)
        );
    }

    @Test
    void it_greets_person() {
        String expectedGreeting = "Hello Sarah";
        when(greeter.greet(anyString())).thenReturn(expectedGreeting);

        String greetingRes = sandbox.hello("Sarah");

        assertEquals(expectedGreeting, greetingRes);
        verify(greeter).greet("Sarah");
    }

    @Test
    void it_sends_greeting_email() {
        String greeting = "Hello Sarah";
        when(greeter.greet(anyString())).thenReturn(greeting);

        sandbox.sendGreetingEmail("Sarah");

        verify(mailSender).send(greeting);
    }

    @ParameterizedTest
    @MethodSource("addTwoNumbersParams")
    void addTwoNumbers_methodSource(int a, int b, int expectedResult) {
        assertEquals(expectedResult, sandbox.add(a, b));
    }

    @ParameterizedTest(name = "the result of {0} + {1} = {2}")
    @CsvSource({
            "1,1, 2",
            "1,4, 5",
            "3,4, 7"
    })
    void addTwoNumbers_CSVSource(int a, int b, int expectedResult) {
        assertEquals(expectedResult, sandbox.add(a, b));
    }
}