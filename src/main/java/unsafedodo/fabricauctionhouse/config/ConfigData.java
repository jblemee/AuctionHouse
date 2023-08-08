package unsafedodo.fabricauctionhouse.config;

public class ConfigData {
    int maxItemsPerPlayer;
    long auctionSecondsDuration;
    int auctionHouseMaxPages;

    public ConfigData(int maxItemsPerPlayer, long auctionSecondsDuration, int auctionHouseMaxPages) {
        this.maxItemsPerPlayer = maxItemsPerPlayer;
        this.auctionSecondsDuration = auctionSecondsDuration;
        this.auctionHouseMaxPages = auctionHouseMaxPages;
    }

    public ConfigData(){
        this(10, 604800L, 50);
    }

    public int getMaxItemsPerPlayer() {
        return maxItemsPerPlayer;
    }

    public long getAuctionSecondsDuration() {
        return auctionSecondsDuration;
    }

    public int getAuctionHouseMaxPages(){
        return auctionHouseMaxPages;
    }
}
