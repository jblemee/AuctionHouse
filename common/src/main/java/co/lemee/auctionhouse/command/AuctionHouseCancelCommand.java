package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.auction.AuctionHouse;
import co.lemee.auctionhouse.auction.AuctionItem;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;


public class AuctionHouseCancelCommand {
    public static int run(CommandContext<CommandSourceStack> context) {
        AuctionHouse personalHouse = AuctionHouseMod.ah.getPlayerAuctionHouse(context.getSource().getPlayer().getStringUUID());

        if (personalHouse.getSize() > 0) {
            for (AuctionItem item : personalHouse.items) {
                AuctionHouseMod.getDatabaseManager().expireItem(item);
            }
            context.getSource().sendSuccess(() -> Component.literal("All your items on auction have been cancelled").withStyle(ChatFormatting.GREEN), false);
        } else
            context.getSource().sendSuccess(() -> Component.literal("You don't have any item on auction").withStyle(ChatFormatting.RED), false);

        return 0;
    }
}
