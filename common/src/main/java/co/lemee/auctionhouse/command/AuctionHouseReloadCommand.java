package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.config.ConfigManager;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class AuctionHouseReloadCommand {
    public static int run(CommandContext<CommandSourceStack> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendSuccess(() -> Component.literal("Reloaded config!").withStyle(ChatFormatting.GREEN), false);
            return 0;
        } else {
            context.getSource().sendFailure(Component.literal("Error accrued while reloading config!").withStyle(ChatFormatting.RED));
            return -1;
        }
    }
}
