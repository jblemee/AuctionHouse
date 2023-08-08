package unsafedodo.fabricauctionhouse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import unsafedodo.fabricauctionhouse.AuctionHouseMain;
import unsafedodo.fabricauctionhouse.auction.AuctionHouse;
import unsafedodo.fabricauctionhouse.auction.AuctionItem;
import unsafedodo.fabricauctionhouse.auction.ExpiredItems;

public class AuctionHouseCancelCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("cancel")
                        .requires(Permissions.require("auctionhouse.cancel", 0))
                        .executes(AuctionHouseCancelCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        AuctionHouse personalHouse = AuctionHouseMain.ah.getPlayerAuctionHouse(context.getSource().getPlayer().getUuidAsString());
        System.out.println("PERSONAL SIZE: "+personalHouse.items.size());
        for(AuctionItem item: personalHouse.items){
            System.out.println(item.getName());
        }
        if(personalHouse.getSize() > 0){
            for(AuctionItem item: personalHouse.items){
                System.out.println("EXPIRE CANCEL");
                AuctionHouseMain.getDatabaseManager().expireItem(item);
            }
            context.getSource().sendFeedback(()-> Text.literal("All your items on auction have been cancelled").formatted(Formatting.GREEN), false);
        } else
            context.getSource().sendFeedback(()-> Text.literal("You don't have any item on auction").formatted(Formatting.RED), false);

        return 0;
    }
}
