package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.gui.GUIExpiredItems;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class AuctionHouseExpiredCommand {
    public static int run(CommandContext<CommandSourceStack> context) {
        GUIExpiredItems guiExpiredItems = new GUIExpiredItems(context.getSource().getPlayer());
        guiExpiredItems.open();
        return 0;
    }
}
