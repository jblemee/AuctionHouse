package co.lemee.auctionhouse.auction;

import java.util.ArrayList;
import java.util.List;

public class ExpiredItems {
    public List<AuctionItem> items;

    public ExpiredItems(List<AuctionItem> items) {
        this.items = items;
    }

    public void addItem(AuctionItem item) {
        items.add(item);
    }

    public void removeItem(AuctionItem item) {
        items.remove(item);
    }

    public int size() {
        return items.size();
    }

    public AuctionItem getItem(int item) {
        return items.get(item);
    }

    public ExpiredItems getPlayerExpiredItems(String uuid) {
        ExpiredItems ei = new ExpiredItems(new ArrayList<>());
        for (AuctionItem ai : items) {
            if (ai.getUuid().equals(uuid))
                ei.addItem(ai);
        }
        return ei;
    }
}
