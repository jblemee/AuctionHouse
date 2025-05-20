package co.lemee.auctionhouse.util;

import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public abstract class EconomyHandler {

    private static final EconomyService service = EconomyService.instance();

    private static final Currency currency = service.currencies().primary();

    public static Account getAccount(UUID uuid) {
        return service.account(currency, uuid).join();
    }

    public static boolean add(Account account, double amount) {
        EconomyTransaction transaction = account.deposit(new BigDecimal(amount));
        return transaction.successful();
    }

    public static boolean remove(Account account, double amount) {
        EconomyTransaction transaction = account.withdraw(new BigDecimal(amount));

        return transaction.successful();
    }

//    public static String getCurrency(){
//        return currency.symbol().toString();
//    }

    public static boolean hasEnoughMoney(Account account, double amount) throws ExecutionException, InterruptedException {
        double balance = getBalance(account);

        return balance >= amount;
    }

    public static double getBalance(Account account) throws ExecutionException, InterruptedException {
        return account.balance().doubleValue();
    }

    public static boolean transfer(Account sender, Account receiver, double amount) {
        boolean removedMoney = remove(sender, amount);
        boolean addMoney = add(receiver, amount);

        if (!removedMoney && addMoney) {
            remove(receiver, amount);
        }

        if (removedMoney && !addMoney) {
            add(sender, amount);
        }

        return removedMoney && addMoney;
    }
}
