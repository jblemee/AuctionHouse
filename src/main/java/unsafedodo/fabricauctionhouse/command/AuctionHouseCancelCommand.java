package unsafedodo.fabricauctionhouse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AuctionHouseCancelCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("cancel")
                        .requires(Permissions.require("auctionhouse.cancel"))
                        .executes(AuctionHouseCancelCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> context) {

        return 0;
    }
}
