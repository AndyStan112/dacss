package grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

public class BankImpl extends BankServiceGrpc.BankServiceImplBase {

    private final Map<String, Double> accounts = new HashMap<>();

    public BankImpl() {
        accounts.put("ACC100", 1000.0);
        accounts.put("ACC200", 500.0);
        accounts.put("ACC300", 0.0);
    }

    @Override
    public synchronized void addAmount(
            AmountRequest request,
            StreamObserver<BalanceResponse> responseObserver
    ) {
        String accountId = request.getAccountId();
        double amount = request.getAmount();

        if (amount <= 0) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Amount must be greater than zero.")
                            .asRuntimeException()
            );
            return;
        }

        double currentBalance = accounts.getOrDefault(accountId, 0.0);
        double newBalance = currentBalance + amount;

        accounts.put(accountId, newBalance);

        System.out.println("Added " + amount + " to account " + accountId);

        BalanceResponse response = BalanceResponse.newBuilder()
                .setAccountId(accountId)
                .setBalance(newBalance)
                .setMessage("Amount added successfully.")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void withdrawAmount(
            AmountRequest request,
            StreamObserver<BalanceResponse> responseObserver
    ) {
        String accountId = request.getAccountId();
        double amount = request.getAmount();

        if (amount <= 0) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Amount must be greater than zero.")
                            .asRuntimeException()
            );
            return;
        }

        if (!accounts.containsKey(accountId)) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Account does not exist: " + accountId)
                            .asRuntimeException()
            );
            return;
        }

        double currentBalance = accounts.get(accountId);

        if (currentBalance < amount) {
            responseObserver.onError(
                    Status.FAILED_PRECONDITION
                            .withDescription("Insufficient funds in account " + accountId)
                            .asRuntimeException()
            );
            return;
        }

        double newBalance = currentBalance - amount;
        accounts.put(accountId, newBalance);

        System.out.println("Withdrew " + amount + " from account " + accountId);

        BalanceResponse response = BalanceResponse.newBuilder()
                .setAccountId(accountId)
                .setBalance(newBalance)
                .setMessage("Amount withdrawn successfully.")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void transferAmount(
            TransferRequest request,
            StreamObserver<TransferResponse> responseObserver
    ) {
        String fromAccountId = request.getFromAccountId();
        String toAccountId = request.getToAccountId();
        double amount = request.getAmount();

        if (amount <= 0) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Amount must be greater than zero.")
                            .asRuntimeException()
            );
            return;
        }

        if (!accounts.containsKey(fromAccountId)) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Source account does not exist: " + fromAccountId)
                            .asRuntimeException()
            );
            return;
        }

        double fromBalance = accounts.get(fromAccountId);

        if (fromBalance < amount) {
            responseObserver.onError(
                    Status.FAILED_PRECONDITION
                            .withDescription("Insufficient funds in account " + fromAccountId)
                            .asRuntimeException()
            );
            return;
        }

        double toBalance = accounts.getOrDefault(toAccountId, 0.0);

        double newFromBalance = fromBalance - amount;
        double newToBalance = toBalance + amount;

        accounts.put(fromAccountId, newFromBalance);
        accounts.put(toAccountId, newToBalance);

        System.out.println(
                "Transferred " + amount +
                " from " + fromAccountId +
                " to " + toAccountId
        );

        TransferResponse response = TransferResponse.newBuilder()
                .setFromAccountId(fromAccountId)
                .setToAccountId(toAccountId)
                .setFromBalance(newFromBalance)
                .setToBalance(newToBalance)
                .setMessage("Transfer completed successfully.")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public synchronized void getBalance(
            BalanceRequest request,
            StreamObserver<BalanceResponse> responseObserver
    ) {
        String accountId = request.getAccountId();

        if (!accounts.containsKey(accountId)) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Account does not exist: " + accountId)
                            .asRuntimeException()
            );
            return;
        }

        double balance = accounts.get(accountId);

        System.out.println("Queried balance for account " + accountId);

        BalanceResponse response = BalanceResponse.newBuilder()
                .setAccountId(accountId)
                .setBalance(balance)
                .setMessage("Balance retrieved successfully.")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}