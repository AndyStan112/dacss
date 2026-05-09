package course_examples.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;


// The server program - creates and hosts the remote object
public class MathServer {

    public static void main(String[] args) {
        try {
            MathService mathService = new MathServiceImpl();

            Registry registry = locateOrCreateRegistry(1099);

            if (registry == null) {
                System.out.println("No RMI registry available -  PROGRAM TERMINATED");
                return;
            }

            registry.rebind("MathService", mathService);
            System.out.println("MathService is running and bound to registry.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Registry locateOrCreateRegistry(int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(port);
            registry.list(); // test if registry is alive
            System.out.println("Located existing RMI registry ");
            return registry;

        } catch (RemoteException e) {
            try {
                Registry registry = LocateRegistry.createRegistry(port);
                System.out.println("Created new RMI registry on port " + port);
                return registry;

            } catch (RemoteException ex) {
                System.err.println("Failed to create RMI registry!");
                return null;
            }
        }
    }
}