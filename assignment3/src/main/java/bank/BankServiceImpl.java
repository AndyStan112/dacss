package bank;

import java.util.HashMap;
import java.util.Map;

public class BankServiceImpl implements BankService {

    private final Map<String, Integer> accounts = new HashMap<>();

    public BankServiceImpl() {
        accounts.put("ACC100", 1000);
        accounts.put("ACC200", 500);
        accounts.put("ACC300", 0);
    }

    @Override
    public synchronized Integer addAmount(String accountId, Integer amount) {
        validateAmount(amount);

        Integer currentBalance = accounts.getOrDefault(accountId, 0);
        Integer newBalance = currentBalance + amount;

        accounts.put(accountId, newBalance);

        System.out.println("Added " + amount + " to " + accountId);

        return newBalance;
    }

    @Override
    public synchronized Integer withdrawAmount(String accountId, Integer amount) {
        validateAmount(amount);
        ensureAccountExists(accountId);

        Integer currentBalance = accounts.get(accountId);

        if (currentBalance < amount) {
            throw new RuntimeException("Insufficient funds in account " + accountId);
        }

        Integer newBalance = currentBalance - amount;
        accounts.put(accountId, newBalance);

        System.out.println("Withdrew " + amount + " from " + accountId);

        return newBalance;
    }

    @Override
    public synchronized String transferAmount(String fromAccountId, String toAccountId, Integer amount) {
        validateAmount(amount);
        ensureAccountExists(fromAccountId);

        Integer fromBalance = accounts.get(fromAccountId);

        if (fromBalance < amount) {
            throw new RuntimeException("Insufficient funds in account " + fromAccountId);
        }

        Integer toBalance = accounts.getOrDefault(toAccountId, 0);

        accounts.put(fromAccountId, fromBalance - amount);
        accounts.put(toAccountId, toBalance + amount);

        System.out.println(
                "Transferred " + amount +
                " from " + fromAccountId +
                " to " + toAccountId
        );

        return "Transfer successful. "
                + fromAccountId + " balance = " + accounts.get(fromAccountId)
                + ", "
                + toAccountId + " balance = " + accounts.get(toAccountId);
    }

    @Override
    public synchronized Integer getBalance(String accountId) {
        ensureAccountExists(accountId);
        return accounts.get(accountId);
    }

    private void validateAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
    }

    private void ensureAccountExists(String accountId) {
        if (!accounts.containsKey(accountId)) {
            throw new RuntimeException("Account does not exist: " + accountId);
        }
    }
}