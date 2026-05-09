package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// The server program - creates and hosts the remote object
public class BankServer {

    public static void main(String[] args) {
        try {
            BankService bankService = new BankServiceImpl();

            Registry registry = locateOrCreateRegistry(1099);

            if (registry == null) {
                System.out.println("No RMI registry available - PROGRAM TERMINATED");
                return;
            }

            registry.rebind("BankService", bankService);
            System.out.println("BankService is running and bound to registry.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Registry locateOrCreateRegistry(int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(port);
            registry.list(); // test if registry is alive
            System.out.println("Located existing RMI registry");
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