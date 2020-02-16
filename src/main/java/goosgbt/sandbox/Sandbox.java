package goosgbt.sandbox;

public class Sandbox {
    private final Greeter greeter;
    private final MailSender mailSender;

    public Sandbox(Greeter greeter, MailSender mailSender) {
        this.greeter = greeter;
        this.mailSender = mailSender;
    }

    public void sendGreetingEmail(String name) {
        mailSender.send(hello(name));
    }

    public String hello(String name) {
        return greeter.greet(name);
    }
}
