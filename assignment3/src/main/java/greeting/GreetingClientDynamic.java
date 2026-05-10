package greeting;

import custom_rmi.RemoteRegistry;
import custom_rmi.StubMode;

public class GreetingClientDynamic {

    public static void main(String[] args) {
        RemoteRegistry registry =
                RemoteRegistry.getRegistry("localhost", 9002, StubMode.DYNAMIC);

        GreetingService greetingService =
                (GreetingService) registry.lookup("GreetingService");

        String hello = greetingService.sayHello("Andy");
        System.out.println("sayHello result: " + hello);

        String repeated = greetingService.repeat("Hi", -3);
        System.out.println("repeat result: " + repeated);

        Integer length = greetingService.length("Distributed Systems");
        System.out.println("length result: " + length);
    }
}