package greeting;

import custom_rmi.RemoteRegistry;

public class GreetingServer {

    public static void main(String[] args) {
        GreetingService greetingService = new GreetingServiceImpl();

        RemoteRegistry registry = RemoteRegistry.createRegistry(9002);

        registry.rebind("GreetingService", greetingService);

        System.out.println("GreetingService is running and bound to custom registry on port 9002.");
    }
}