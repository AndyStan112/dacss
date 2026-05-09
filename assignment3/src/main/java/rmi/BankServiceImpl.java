package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

// Implementation of the remote interface
public class BankServiceImpl extends UnicastRemoteObject implements BankService {

    private final Map<String, Double> accounts;

    protected BankServiceImpl() throws RemoteException {
        super();
        accounts = new HashMap<>();

        accounts.put("ACC100", 1000.0);
        accounts.put("ACC200", 500.0);
        accounts.put("ACC300", 0.0);
    }

    @Override
    public synchronized void addAmount(String accountId, double amount) throws RemoteException {
        validateAmount(amount);

        double currentBalance = accounts.getOrDefault(accountId, 0.0);
        accounts.put(accountId, currentBalance + amount);

        System.out.println("Added " + amount + " to account " + accountId);
    }

    @Override
    public synchronized void withdrawAmount(String accountId, double amount) throws RemoteException {
        validateAmount(amount);
        ensureAccountExists(accountId);

        double currentBalance = accounts.get(accountId);

        if (currentBalance < amount) {
            throw new RemoteException("Insufficient funds in account " + accountId);
        }

        accounts.put(accountId, currentBalance - amount);

        System.out.println("Withdrew " + amount + " from account " + accountId);
    }

    @Override
    public synchronized void transferAmount(
            String fromAccountId,
            String toAccountId,
            double amount
    ) throws RemoteException {

        validateAmount(amount);
        ensureAccountExists(fromAccountId);

        double fromBalance = accounts.get(fromAccountId);

        if (fromBalance < amount) {
            throw new RemoteException("Insufficient funds in account " + fromAccountId);
        }

        double toBalance = accounts.getOrDefault(toAccountId, 0.0);

        accounts.put(fromAccountId, fromBalance - amount);
        accounts.put(toAccountId, toBalance + amount);

        System.out.println(
                "Transferred " + amount +
                " from account " + fromAccountId +
                " to account " + toAccountId
        );
    }

    @Override
    public synchronized double getBalance(String accountId) throws RemoteException {
        ensureAccountExists(accountId);
        return accounts.get(accountId);
    }

    private void validateAmount(double amount) throws RemoteException {
        if (amount <= 0) {
            throw new RemoteException("Amount must be greater than zero.");
        }
    }

    private void ensureAccountExists(String accountId) throws RemoteException {
        if (!accounts.containsKey(accountId)) {
            throw new RemoteException("Account does not exist: " + accountId);
        }
    }
}