package co.lemee.auctionhouse.auction;

import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static co.lemee.auctionhouse.util.ComponentMapSerializer.deserialize;
import static co.lemee.auctionhouse.util.ComponentMapSerializer.serialize;

public class AuctionItem {
    private final ItemStack itemStack;
    private final String uuid;
    private final String owner;
    private final String nbt;
    private final double price;
    private final int id;
    private long secondsLeft;

    public AuctionItem(int id, String playerUuid, String owner, ItemStack stack, double price, long secondsLeft) {
        this.id = id;
        this.itemStack = stack;
        this.uuid = playerUuid;
        this.owner = owner;
        this.nbt = serialize(itemStack.getComponents()).getAsString();
        this.price = price;
        this.secondsLeft = secondsLeft;
    }

    public AuctionItem(int id, String playerUuid, String owner, String nbt, String item, int count, double price, long secondsLeft) {
        this.id = id;
        this.itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(item)), count);
        this.itemStack.applyComponentsFrom(deserialize(JsonParser.parseString(nbt)));
        this.nbt = nbt;
        this.uuid = playerUuid;
        this.owner = owner;
        this.price = price;
        this.secondsLeft = secondsLeft;
    }

    public int getId() {
        return id;
    }

    public String getNbt() {
        return nbt;
    }

    public long getSecondsLeft() {
        return secondsLeft;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getUuid() {
        return uuid;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return Registries.ITEM.getId(itemStack.getItem()).toString();
    }

    public String getDisplayName() {
        return (this.itemStack.getName().getString());
    }

    public double getPrice() {
        return price;
    }

    public String getTimeLeft() {
        long seconds = secondsLeft;
        int days = (int) (seconds / 86400);
        seconds -= days * 86400L;
        int hours = (int) (seconds / 3600);
        seconds -= hours * 3600L;
        int minutes = (int) (seconds / 60);
        seconds -= minutes * 60L;
        if (days > 0) {
            return String.format("%02dd:%02dh:%02dm", days, hours, minutes);
        } else {
            if (hours > 0) {
                return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
            }
        }
        return (minutes > 0) ? String.format("%02dm:%02ds", minutes, seconds) : (seconds + "s");
    }

    public boolean tickDeath() {
        if (secondsLeft > 0) {
            secondsLeft--;
        }
        return secondsLeft == 0;
    }
}
