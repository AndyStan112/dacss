package bank;

import custom_rmi.RemoteRegistry;
import custom_rmi.StubMode;

public class BankClientDynamic {

    public static void main(String[] args) {
        RemoteRegistry registry =
                RemoteRegistry.getRegistry("localhost", 9001, StubMode.DYNAMIC);

        BankService bankService =
                (BankService) registry.lookup("BankService");

        System.out.println("Initial balances:");
        System.out.println("ACC100 = " + bankService.getBalance("ACC100"));
        System.out.println("ACC200 = " + bankService.getBalance("ACC200"));
        System.out.println("ACC300 = " + bankService.getBalance("ACC300"));

        System.out.println();

        Integer balance1 = bankService.addAmount("ACC100", 250);
        System.out.println("After adding 250 to ACC100: " + balance1);

        Integer balance2 = bankService.withdrawAmount("ACC200", 100);
        System.out.println("After withdrawing 100 from ACC200: " + balance2);

        String transferResult =
                bankService.transferAmount("ACC100", "ACC300", 300);

        System.out.println(transferResult);

        System.out.println();

        System.out.println("Final balances:");
        System.out.println("ACC100 = " + bankService.getBalance("ACC100"));
        System.out.println("ACC200 = " + bankService.getBalance("ACC200"));
        System.out.println("ACC300 = " + bankService.getBalance("ACC300"));
    }
}