package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.auction.AuctionItem;
import co.lemee.auctionhouse.auction.ExpiredItems;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Iterator;

public class AuctionHouseReturnCommand {

    public static int run(CommandContext<CommandSourceStack> context) {
        ExpiredItems personalExpired = AuctionHouseMod.ei.getPlayerExpiredItems(context.getSource().getPlayer().getStringUUID());
        if (personalExpired.size() > 0) {
            ArrayList<Integer> emptySlots = new ArrayList<>() {
            };

            for (int i = 0; i < 36; i++) {
                if (context.getSource().getPlayer().getInventory().getItem(i).isEmpty())
                    emptySlots.add(i);
            }
            if (emptySlots.size() >= personalExpired.items.size()) {
                Iterator<Integer> iterator = emptySlots.listIterator();
                for (AuctionItem item : personalExpired.items) {
                    AuctionHouseMod.getDatabaseManager().removeItemFromExpired(item);
                    context.getSource().getPlayer().getInventory().setItem(iterator.next(), item.getItemStack());
                }
                context.getSource().sendSuccess(() -> Component.literal("All expired items have been added to your inventory").withStyle(ChatFormatting.GREEN), false);
                return 0;

            } else {
                context.getSource().sendSuccess(() -> Component.literal("You don't have enough space in your inventory").withStyle(ChatFormatting.RED), false);
                return -1;
            }

        } else {
            context.getSource().sendSuccess(() -> Component.literal("You don't have any expired item").withStyle(ChatFormatting.RED), false);
            return -1;
        }

    }
}
