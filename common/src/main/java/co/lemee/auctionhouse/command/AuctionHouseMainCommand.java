package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.gui.GUIAuctionHouse;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class AuctionHouseMainCommand {
    public static int run(CommandContext<CommandSourceStack> context) {
        GUIAuctionHouse guiAuctionHouse = new GUIAuctionHouse(context.getSource().getPlayer());
        guiAuctionHouse.open();
        return 0;
    }
}
