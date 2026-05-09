package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BankClient {

    public static void main(String[] args) {
        try {
            // Connect to RMI registry on localhost
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Look up the remote service
            BankService bankService = (BankService) registry.lookup("BankService");

            String account1 = "ACC100";
            String account2 = "ACC200";
            String account3 = "ACC300";

            System.out.println("Initial balances:");
            System.out.println(account1 + " balance = " + bankService.getBalance(account1));
            System.out.println(account2 + " balance = " + bankService.getBalance(account2));
            System.out.println(account3 + " balance = " + bankService.getBalance(account3));

            System.out.println();

            bankService.addAmount(account1, 250.0);
            System.out.println("After adding 250 to " + account1 + ":");
            System.out.println(account1 + " balance = " + bankService.getBalance(account1));

            System.out.println();

            bankService.withdrawAmount(account2, 100.0);
            System.out.println("After withdrawing 100 from " + account2 + ":");
            System.out.println(account2 + " balance = " + bankService.getBalance(account2));

            System.out.println();

            bankService.transferAmount(account1, account3, 300.0);
            System.out.println("After transferring 300 from " + account1 + " to " + account3 + ":");
            System.out.println(account1 + " balance = " + bankService.getBalance(account1));
            System.out.println(account3 + " balance = " + bankService.getBalance(account3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}