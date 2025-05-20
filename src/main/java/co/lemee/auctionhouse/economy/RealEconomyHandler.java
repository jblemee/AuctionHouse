package co.lemee.auctionhouse.economy;


import co.lemee.realeconomy.account.Account;
import co.lemee.realeconomy.account.AccountManager;
import co.lemee.realeconomy.config.ConfigManager;
import co.lemee.realeconomy.currency.Currency;

import java.util.UUID;

public class RealEconomyHandler extends EconomyHandler {
    private static final Currency currency = ConfigManager.getConfig().getCurrencyByName(ConfigManager.getConfig().getDefaultCurrency());

    private static Account getAccount(UUID uuid) {
        return AccountManager.getAccount(uuid);
    }

    protected boolean add(UUID accountUUID, double amount) {
        Account account = getAccount(accountUUID);
        return account.add(currency, (float) amount);
    }

    protected boolean remove(UUID accountUUID, double amount) {
        Account account = getAccount(accountUUID);
        return account.remove(currency, (float) amount);
    }

    public double getBalance(UUID accountUUID) {
        Account account = getAccount(accountUUID);
        return account.getBalance(currency);
    }
}
