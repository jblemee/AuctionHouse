package unsafedodo.fabricauctionhouse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AuctionHouseHelpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("help")
                        .requires(Permissions.require("auctionhouse.help", 0))
                        .executes(AuctionHouseHelpCommand::run)));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        String msg = """
                /ah
                /ah expired
                /ah sell <price>
                /ah cancel
                /ah return
                /ah selling
                /ah reload""";

        context.getSource().sendFeedback(()-> Text.literal(msg).formatted(Formatting.YELLOW), false);

        return 0;
    }
}
