package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AuctionHouseReloadCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("reload")
                        .requires(Permissions.require("auctionhouse.reload", 4))
                        .executes(AuctionHouseReloadCommand::run)));
    }

    public static int run(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(() -> Text.literal("Reloaded config!").formatted(Formatting.GREEN), false);
            return 0;
        } else {
            context.getSource().sendError(Text.literal("Error accrued while reloading config!").formatted(Formatting.RED));
            return -1;
        }
    }
}
