package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.auction.AuctionHouse;
import co.lemee.auctionhouse.auction.AuctionItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AuctionHouseCancelCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("cancel")
                        .requires(Permissions.require("auctionhouse.cancel", 0))
                        .executes(AuctionHouseCancelCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        AuctionHouse personalHouse = AuctionHouseMod.ah.getPlayerAuctionHouse(context.getSource().getPlayer().getUuidAsString());

        if (personalHouse.getSize() > 0) {
            for (AuctionItem item : personalHouse.items) {
                AuctionHouseMod.getDatabaseManager().expireItem(item);
            }
            context.getSource().sendFeedback(() -> Text.literal("All your items on auction have been cancelled").formatted(Formatting.GREEN), false);
        } else
            context.getSource().sendFeedback(() -> Text.literal("You don't have any item on auction").formatted(Formatting.RED), false);

        return 0;
    }
}
