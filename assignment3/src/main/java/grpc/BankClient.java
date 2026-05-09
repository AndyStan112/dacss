package grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BankClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        BankServiceGrpc.BankServiceBlockingStub stub =
                BankServiceGrpc.newBlockingStub(channel);

        try {
            String account1 = "ACC100";
            String account2 = "ACC200";
            String account3 = "ACC300";

            System.out.println("Initial balances:");
            printBalance(stub, account1);
            printBalance(stub, account2);
            printBalance(stub, account3);

            System.out.println();

            BalanceResponse addResponse = stub.addAmount(
                    AmountRequest.newBuilder()
                            .setAccountId(account1)
                            .setAmount(250.0)
                            .build()
            );

            System.out.println("After adding 250 to " + account1 + ":");
            System.out.println(addResponse.getAccountId() + " balance = " + addResponse.getBalance());

            System.out.println();

            BalanceResponse withdrawResponse = stub.withdrawAmount(
                    AmountRequest.newBuilder()
                            .setAccountId(account2)
                            .setAmount(100.0)
                            .build()
            );

            System.out.println("After withdrawing 100 from " + account2 + ":");
            System.out.println(withdrawResponse.getAccountId() + " balance = " + withdrawResponse.getBalance());

            System.out.println();

            TransferResponse transferResponse = stub.transferAmount(
                    TransferRequest.newBuilder()
                            .setFromAccountId(account1)
                            .setToAccountId(account3)
                            .setAmount(300.0)
                            .build()
            );

            System.out.println("After transferring 300 from " + account1 + " to " + account3 + ":");
            System.out.println(transferResponse.getFromAccountId() + " balance = " + transferResponse.getFromBalance());
            System.out.println(transferResponse.getToAccountId() + " balance = " + transferResponse.getToBalance());

            System.out.println();

            System.out.println("Final balances:");
            printBalance(stub, account1);
            printBalance(stub, account2);
            printBalance(stub, account3);

        } catch (StatusRuntimeException e) {
            System.err.println("gRPC error: " + e.getStatus().getDescription());
        } finally {
            channel.shutdown();
        }
    }

    private static void printBalance(BankServiceGrpc.BankServiceBlockingStub stub, String accountId) {
        BalanceResponse response = stub.getBalance(
                BalanceRequest.newBuilder()
                        .setAccountId(accountId)
                        .build()
        );

        System.out.println(accountId + " balance = " + response.getBalance());
    }
}