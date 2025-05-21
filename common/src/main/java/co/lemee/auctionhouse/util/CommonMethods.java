package co.lemee.auctionhouse.util;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.auction.AuctionHouse;
import co.lemee.auctionhouse.auction.ExpiredItems;
import co.lemee.auctionhouse.sql.SQLiteDatabaseManager;

public class CommonMethods {
    public static void reloadHouse() {
        AuctionHouseMod.ah = new AuctionHouse(SQLiteDatabaseManager.getItemList());
    }

    public static void reloadExpired() {
        AuctionHouseMod.ei = new ExpiredItems(SQLiteDatabaseManager.getExpiredItemsList());
    }
}
