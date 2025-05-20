package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.gui.GUIAuctionHouse;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class AuctionHouseMainCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("ah")
                .requires(Permissions.require("auctionhouse.main", 0))
                .executes(AuctionHouseMainCommand::run));
    }

    public static int run(CommandContext<ServerCommandSource> context) {
        GUIAuctionHouse guiAuctionHouse = new GUIAuctionHouse(context.getSource().getPlayer());
        guiAuctionHouse.open();
        return 0;
    }
}
