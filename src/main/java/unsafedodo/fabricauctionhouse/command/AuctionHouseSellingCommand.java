package unsafedodo.fabricauctionhouse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import unsafedodo.fabricauctionhouse.gui.GUIPersonalAuctionHouse;

public class AuctionHouseSellingCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("selling")
                        .requires(Permissions.require("auctionhouse.selling", 0))
                        .executes(AuctionHouseSellingCommand::run)));
    }

    public static int run(CommandContext<ServerCommandSource> context){
        GUIPersonalAuctionHouse guiPersonalAuctionHouse = new GUIPersonalAuctionHouse(context.getSource().getPlayer());
        guiPersonalAuctionHouse.open();
        return 0;
    }
}
