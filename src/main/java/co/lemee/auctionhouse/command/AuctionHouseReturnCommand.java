package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.auction.AuctionItem;
import co.lemee.auctionhouse.auction.ExpiredItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Iterator;

public class AuctionHouseReturnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("return")
                        .requires(Permissions.require("auctionhouse.return", 0))
                        .executes(AuctionHouseReturnCommand::run)));
    }

    public static int run(CommandContext<ServerCommandSource> context) {
        ExpiredItems personalExpired = AuctionHouseMod.ei.getPlayerExpiredItems(context.getSource().getPlayer().getUuidAsString());
        if (personalExpired.size() > 0) {
            ArrayList<Integer> emptySlots = new ArrayList<>() {
            };

            for (int i = 0; i < 36; i++) {
                if (context.getSource().getPlayer().getInventory().getStack(i).isEmpty())
                    emptySlots.add(i);
            }
            if (emptySlots.size() >= personalExpired.items.size()) {
                Iterator<Integer> iterator = emptySlots.listIterator();
                for (AuctionItem item : personalExpired.items) {
                    AuctionHouseMod.getDatabaseManager().removeItemFromExpired(item);
                    context.getSource().getPlayer().getInventory().setStack(iterator.next(), item.getItemStack());
                }
                context.getSource().sendFeedback(() -> Text.literal("All expired items have been added to your inventory").formatted(Formatting.GREEN), false);
                return 0;

            } else {
                context.getSource().sendFeedback(() -> Text.literal("You don't have enough space in your inventory").formatted(Formatting.RED), false);
                return -1;
            }

        } else {
            context.getSource().sendFeedback(() -> Text.literal("You don't have any expired item").formatted(Formatting.RED), false);
            return -1;
        }

    }
}
