package unsafedodo.fabricauctionhouse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import unsafedodo.fabricauctionhouse.AuctionHouseMain;
import unsafedodo.fabricauctionhouse.auction.AuctionHouse;
import unsafedodo.fabricauctionhouse.auction.AuctionItem;
import unsafedodo.fabricauctionhouse.auction.ExpiredItems;

public class ShowPersonalCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("show")
                        .executes(ShowPersonalCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        System.out.println("PUBLIC: \n\n");
        for(AuctionItem item: AuctionHouseMain.ah.items){
            System.out.println(item.getName());
        }

        System.out.println("\n\nPUBLIC EXPIRED: \n\n");
        for (AuctionItem item: AuctionHouseMain.ei.items) {
            System.out.println(item.getName());
        }

        AuctionHouse personal = AuctionHouseMain.ah.getPlayerAuctionHouse(context.getSource().getPlayer().getUuidAsString());
        ExpiredItems personalExpired = AuctionHouseMain.ei.getPlayerExpiredItems(context.getSource().getPlayer().getUuidAsString());
        System.out.println("PERSONAL: \n");
        for (AuctionItem item :
                personal.items) {
            System.out.println(item.getName());
        }


        System.out.println("PERSONAL EXPIRED: \n");
        for (AuctionItem item :
                personalExpired.items) {
            System.out.println(item.getName());
        }
        return 0;
    }
}
