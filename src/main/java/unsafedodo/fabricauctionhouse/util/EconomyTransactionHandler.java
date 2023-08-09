package unsafedodo.fabricauctionhouse.util;

import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.octoecon.api.user.UniqueUser;

import java.util.UUID;

public class EconomyTransactionHandler implements EconomyEvents.EconomyChange {

    static Economy currentEconomy;
    @Override
    public void onEconomyChanged(Economy economy) {
        currentEconomy = economy;
    }

    public static double getBalanceFromUuid(String uuid){
        UniqueUser userBalance = currentEconomy.getOrCreatePlayerAccount(UUID.fromString(uuid));
        if(userBalance != null)
            return userBalance.getBalance(currentEconomy.getDefaultCurrency());

        return -1;
    }
    public static boolean purchaseItem(String uuid, double price){
        UniqueUser userBalance = currentEconomy.getOrCreatePlayerAccount(UUID.fromString(uuid));
        if(userBalance != null){
            if(userBalance.getBalance(currentEconomy.getDefaultCurrency()) >= price){
                userBalance.setBalance(currentEconomy.getDefaultCurrency(), userBalance.getBalance(currentEconomy.getDefaultCurrency()) - price);
                return true;
            } else
                return false;
        }
        return false;
    }

    public static boolean getMoneyFromPurchase(String uuid, double price){
        UniqueUser userBalance = currentEconomy.getOrCreatePlayerAccount(UUID.fromString(uuid));
        if(userBalance != null){
            if(userBalance.getBalance(currentEconomy.getDefaultCurrency()) <= Double.MAX_VALUE && userBalance.getBalance(currentEconomy.getDefaultCurrency()) >= 0){
                userBalance.setBalance(currentEconomy.getDefaultCurrency(), userBalance.getBalance(currentEconomy.getDefaultCurrency()) + price);
                return true;
            }
            return false;
        }
        return false;
    }
}
