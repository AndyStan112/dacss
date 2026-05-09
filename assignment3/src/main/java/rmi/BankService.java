package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Remote interface
public interface BankService extends Remote {

    void addAmount(String accountId, double amount) throws RemoteException;

    void withdrawAmount(String accountId, double amount) throws RemoteException;

    void transferAmount(String fromAccountId, String toAccountId, double amount) throws RemoteException;

    double getBalance(String accountId) throws RemoteException;
}