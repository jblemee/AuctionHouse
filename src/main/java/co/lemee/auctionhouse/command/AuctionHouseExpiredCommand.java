package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.gui.GUIExpiredItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class AuctionHouseExpiredCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("expired")
                        .requires(Permissions.require("auctionhouse.expired", 0))
                        .executes(AuctionHouseExpiredCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        GUIExpiredItems guiExpiredItems = new GUIExpiredItems(context.getSource().getPlayer());
        guiExpiredItems.open();
        return 0;
    }
}
