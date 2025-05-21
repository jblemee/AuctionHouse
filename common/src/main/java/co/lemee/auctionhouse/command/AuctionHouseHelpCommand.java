package co.lemee.auctionhouse.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class AuctionHouseHelpCommand {
    public static int run(CommandContext<CommandSourceStack> context) {
        String msg = """
                /ah
                /ah expired
                /ah sell <price>
                /ah cancel
                /ah return
                /ah selling
                /ah reload""";

        context.getSource().sendSuccess(() -> Component.literal(msg).withStyle(ChatFormatting.YELLOW), false);

        return 0;
    }
}
