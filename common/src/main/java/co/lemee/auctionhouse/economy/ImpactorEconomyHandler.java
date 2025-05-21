package co.lemee.auctionhouse.economy;

import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;

import java.math.BigDecimal;
import java.util.UUID;

public class ImpactorEconomyHandler extends EconomyHandler {

    private static final EconomyService service = EconomyService.instance();
    private static final Currency currency = service.currencies().primary();

    private static Account getAccount(UUID uuid) {
        return service.account(currency, uuid).join();
    }

    protected boolean add(UUID accountUUID, double amount) {
        Account account = getAccount(accountUUID);
        EconomyTransaction transaction = account.deposit(new BigDecimal(amount));
        return transaction.successful();
    }

    protected boolean remove(UUID accountUUID, double amount) {
        Account account = getAccount(accountUUID);
        EconomyTransaction transaction = account.withdraw(new BigDecimal(amount));

        return transaction.successful();
    }

    public double getBalance(UUID accountUUID) {
        Account account = getAccount(accountUUID);
        return account.balance().doubleValue();
    }
}
