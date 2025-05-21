package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.gui.GUIPersonalAuctionHouse;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class AuctionHouseSellingCommand {
    public static int run(CommandContext<CommandSourceStack> context) {
        GUIPersonalAuctionHouse guiPersonalAuctionHouse = new GUIPersonalAuctionHouse(context.getSource().getPlayer());
        guiPersonalAuctionHouse.open();
        return 0;
    }
}
