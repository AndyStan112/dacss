package bank;

public interface BankService {

    Integer addAmount(String accountId, Integer amount);

    Integer withdrawAmount(String accountId, Integer amount);

    String transferAmount(String fromAccountId, String toAccountId, Integer amount);

    Integer getBalance(String accountId);
}