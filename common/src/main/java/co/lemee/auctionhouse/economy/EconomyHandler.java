package co.lemee.auctionhouse.economy;

import co.lemee.auctionhouse.AuctionHouseMod;

import java.util.UUID;

import static co.lemee.auctionhouse.AuctionHouseMod.LOGGER;

public abstract class EconomyHandler {

    private static EconomyHandler INSTANCE;

    public static EconomyHandler getInstance() {
        if(INSTANCE == null){
            if(AuctionHouseMod.realeconomy) {
                LOGGER.info("RealEconomy detected, enabling real economy integration");
                INSTANCE = new RealEconomyHandler();
            } else if(AuctionHouseMod.impactor){
                LOGGER.warn("Impactor detected, enabling impactor economy integration");
                INSTANCE =  new ImpactorEconomyHandler();
            } else {
                LOGGER.warn("No economy plugin detected, please install Impactor or RealEconomy");
                INSTANCE =  new ImpactorEconomyHandler();
            }
        }

        return INSTANCE;
    }

    protected abstract boolean add(UUID accountUUID, double amount) ;
    protected abstract boolean remove(UUID accountUUID, double amount);
    public abstract double getBalance(UUID accountUUID);

    public final boolean transfer(UUID senderUUID, UUID receiverUUID, double amount) {
        boolean removedMoney = remove(senderUUID, amount);
        boolean addMoney = add(receiverUUID, amount);

        if (!removedMoney && addMoney) {
            remove(receiverUUID, amount);
        }

        if (removedMoney && !addMoney) {
            add(senderUUID, amount);
        }

        return removedMoney && addMoney;
    }
}
