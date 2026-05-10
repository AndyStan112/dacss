package greeting;

public interface GreetingService {

    String sayHello(String name);

    String repeat(String text, Integer times);

    Integer length(String text);
}