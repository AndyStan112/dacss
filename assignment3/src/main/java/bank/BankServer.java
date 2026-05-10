package bank;


import custom_rmi.RemoteRegistry;

public class BankServer {

    public static void main(String[] args) {
        BankService bankService = new BankServiceImpl();

        RemoteRegistry registry = RemoteRegistry.createRegistry(9001);

        registry.rebind("BankService", bankService);

        System.out.println("BankService is running and bound to custom registry.");
    }
}