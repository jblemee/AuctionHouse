package unsafedodo.fabricauctionhouse.auction;

import unsafedodo.fabricauctionhouse.AuctionHouseMain;
import unsafedodo.fabricauctionhouse.config.ConfigData;
import unsafedodo.fabricauctionhouse.config.ConfigManager;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AuctionHouse {
    public ArrayList<AuctionItem> items;
    int tick = 0;

    public AuctionHouse(ArrayList<AuctionItem> items){
        this.items = items;
    }

    public boolean canAddItems() throws FileNotFoundException, UnsupportedEncodingException {
        return items.size() <= ConfigManager.getConfigData(ConfigManager.configFile).getAuctionHouseMaxPages()*36;
    }

    public void addItem(AuctionItem item){
        items.add(item);
    }

    public AuctionItem getItem(int item){
        return items.get(item);
    }

    public void removeItem(AuctionItem item) {
        items.remove(item);
    }

    public void tick() {
        tick++;
        if (tick % 20 == 0) {
            int i = 0;
            while (i < items.size()) {
                if (items.get(i).tickDeath()) {
                    System.out.println("EXPIRE TICK");
                    AuctionHouseMain.getDatabaseManager().expireItem(items.get(i));
                } else {
                    i++;
                }
            }
        }
        if (tick % 300 == 0) {
            for (AuctionItem item: items) {
                AuctionHouseMain.getDatabaseManager().updateTime(item.getId(), item.getSecondsLeft());
            }
        }
    }

    public AuctionHouse getPlayerAuctionHouse(String uuid) {
        AuctionHouse ah = new AuctionHouse(new ArrayList<>());
        for (AuctionItem ai: items) {
            if (ai.getUuid().equals(uuid)) {
                ah.addItem(ai);
            }
        }
        return ah;
    }

    public int getSize(){
        return items.size();
    }
}
