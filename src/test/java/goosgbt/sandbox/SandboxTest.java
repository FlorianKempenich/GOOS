package goosgbt.sandbox;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

class SandboxTest {

    Sandbox sandbox = new Sandbox();

    @Test
    void it_greets() {
        assertThat(sandbox.hello(""), containsString("Hello"));
    }

    @Test
    void it_greets_the_correct_person() {
        assertThat(sandbox.hello("Frank"), containsString("Frank"));
    }
}