package unsafedodo.fabricauctionhouse.sql;

import unsafedodo.fabricauctionhouse.auction.AuctionItem;

public interface DatabaseManager {
    int addItemToAuction(String playerUuid, String owner, String nbt, String item, int count, double price, long secondsLeft);
    int getMostRecentId();
    int playerItemCount(String playeruuid, String table);
    boolean isItemForAuction(int id);
    void updateTime(int id, long seconds);
    void removeItemFromAuction(AuctionItem item);
    void removeItemFromExpired(AuctionItem item);
    void expireItem(AuctionItem item);
}
