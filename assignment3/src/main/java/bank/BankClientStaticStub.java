package bank;

public class BankClientStaticStub {

    public static void main(String[] args) {
        BankService bank = new BankServiceStub(
                "localhost",
                9001,
                "BankService"
        );

        System.out.println("Initial balances:");
        System.out.println("ACC100 = " + bank.getBalance("ACC100"));
        System.out.println("ACC200 = " + bank.getBalance("ACC200"));
        System.out.println("ACC300 = " + bank.getBalance("ACC300"));

        System.out.println();

        Integer newBalance1 = bank.addAmount("ACC100", 250);
        System.out.println("After adding 250 to ACC100: " + newBalance1);

        Integer newBalance2 = bank.withdrawAmount("ACC200", 100);
        System.out.println("After withdrawing 100 from ACC200: " + newBalance2);

        String transferResult = bank.transferAmount("ACC100", "ACC300", 300);
        System.out.println(transferResult);

        System.out.println();

        System.out.println("Final balances:");
        System.out.println("ACC100 = " + bank.getBalance("ACC100"));
        System.out.println("ACC200 = " + bank.getBalance("ACC200"));
        System.out.println("ACC300 = " + bank.getBalance("ACC300"));
    }
}