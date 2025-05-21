package co.lemee.auctionhouse.command;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.config.ConfigData;
import co.lemee.auctionhouse.config.ConfigManager;
import co.lemee.auctionhouse.sql.DatabaseManager;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import static co.lemee.auctionhouse.AuctionHouseMod.LOGGER;
import static co.lemee.auctionhouse.util.ComponentMapSerializer.serialize;

public class AuctionHouseSellCommand {
    public static int run(CommandContext<CommandSourceStack> context) {

        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            if (player.getMainHandItem().isEmpty()) {
                context.getSource().sendFailure(Component.literal("You must be holding an item").withStyle(ChatFormatting.RED));
                return -1;
            }
            DatabaseManager dbm = AuctionHouseMod.getDatabaseManager();
            String playerUuid = player.getStringUUID();
            try {
                if ((dbm.playerItemCount(playerUuid, "auctionhouse") + dbm.playerItemCount(playerUuid, "expireditems")) >= ConfigManager.getConfigData(ConfigManager.configFile).getMaxItemsPerPlayer()) {
                    context.getSource().sendFailure(Component.literal("You have too many items on auction").withStyle(ChatFormatting.RED));
                    return -1;
                }

                if (AuctionHouseMod.ah.canAddItems()) {
                    double price = DoubleArgumentType.getDouble(context, "price");
                    ItemStack itemInHand = player.getMainHandItem();
                    String item = BuiltInRegistries.ITEM.getKey(itemInHand.getItem()).toString();
                    ConfigData configData = ConfigManager.getConfigData(ConfigManager.configFile);
                    AuctionHouseMod.getDatabaseManager().addItemToAuction(playerUuid, player.getName().getString(), serialize(itemInHand.getComponents()).toString(), item, itemInHand.getCount(), price, configData.getAuctionSecondsDuration());
                    player.getInventory().removeItem(player.getInventory().findSlotMatchingItem(itemInHand), itemInHand.getCount());
                    context.getSource().sendSuccess(() -> Component.literal(String.format("Item successfully added to auction house for %.2f $", price)).withStyle(ChatFormatting.GREEN), false);
                    return 0;
                }

            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                LOGGER.error("Failed to add item to auction", e);
            }

            context.getSource().sendFailure(Component.literal("The auction house is full!").withStyle(ChatFormatting.RED));

            return -1;
        }
        return -1;
    }
}
