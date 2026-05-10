package greeting;

public class GreetingServiceImpl implements GreetingService {

    @Override
    public String sayHello(String name) {
        System.out.println("GreetingServiceImpl.sayHello called with name = " + name);

        return "Hello, " + name + "!";
    }

    @Override
    public String repeat(String text, Integer times) {
        System.out.println(
                "GreetingServiceImpl.repeat called with text = "
                        + text
                        + ", times = "
                        + times
        );

        if (times == null || times < 0) {
            throw new RuntimeException("times must be a non-negative integer");
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < times; i++) {
            result.append(text);

            if (i < times - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    @Override
    public Integer length(String text) {
        System.out.println("GreetingServiceImpl.length called with text = " + text);

        if (text == null) {
            return 0;
        }

        return text.length();
    }
}