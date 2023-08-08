package unsafedodo.fabricauctionhouse.util;

import unsafedodo.fabricauctionhouse.AuctionHouseMain;
import unsafedodo.fabricauctionhouse.auction.AuctionHouse;
import unsafedodo.fabricauctionhouse.auction.ExpiredItems;
import unsafedodo.fabricauctionhouse.sql.SQLiteDatabaseManager;

public class CommonMethods {
    public static void reloadHouse(){
        AuctionHouseMain.ah = new AuctionHouse(SQLiteDatabaseManager.getItemList());
    }

    public static void reloadExpired(){
        AuctionHouseMain.ei = new ExpiredItems(SQLiteDatabaseManager.getExpiredItemsList());
    }
}
