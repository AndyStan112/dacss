package course_examples.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MathClient {
    public static void main(String[] args) {
        try {
            // Connect to RMI registry on localhost
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Look up the remote service
            MathService mathService = (MathService) registry.lookup("MathService");

            // Call remote methods
            int sum = mathService.add(5, 3);
            int product = mathService.mult(5, 3);

            System.out.println("5 + 3 = " + sum);
            System.out.println("5 * 3 = " + product);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}