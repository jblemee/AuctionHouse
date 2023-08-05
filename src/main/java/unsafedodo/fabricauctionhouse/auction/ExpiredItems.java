package unsafedodo.fabricauctionhouse.auction;

import java.util.ArrayList;

public class ExpiredItems {
    private ArrayList<AuctionItem> items;

    public ExpiredItems(ArrayList<AuctionItem> items){
        this.items = items;
    }

    public void addItem(AuctionItem item){
        items.add(item);
    }

    public void removeItem(AuctionItem item){
        items.remove(item);
    }

    public int size(){
        return items.size();
    }

    public AuctionItem getItem(int item){
        return items.get(item);
    }

    public ExpiredItems getPlayerExpiredItems(String uuid){
        ExpiredItems ei = new ExpiredItems(new ArrayList<>());
        for(AuctionItem ai: items){
            if(ai.getUuid().equals(uuid))
                ei.addItem(ai);
        }
        return ei;
    }
}
