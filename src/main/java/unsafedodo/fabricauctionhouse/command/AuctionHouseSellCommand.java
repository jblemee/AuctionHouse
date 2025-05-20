package unsafedodo.fabricauctionhouse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unsafedodo.fabricauctionhouse.AuctionHouseMain;
import unsafedodo.fabricauctionhouse.config.ConfigData;
import unsafedodo.fabricauctionhouse.config.ConfigManager;
import unsafedodo.fabricauctionhouse.sql.DatabaseManager;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import static unsafedodo.fabricauctionhouse.util.ComponentMapSerializer.serialize;

public class AuctionHouseSellCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionHouseSellCommand.class);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("ah")
                .then(CommandManager.literal("sell")
                        .then(CommandManager.argument("price", DoubleArgumentType.doubleArg(0))
                        .requires(Permissions.require("auctionhouse.sell", 0))
                        .executes(AuctionHouseSellCommand::run))));
    }

    public static int run(CommandContext<ServerCommandSource> context)  {

        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player != null){
            if(player.getMainHandStack().isEmpty()){
                context.getSource().sendFeedback(()-> Text.literal("You must be holding an item").formatted(Formatting.RED), false);
                return -1;
            }
            DatabaseManager dbm = AuctionHouseMain.getDatabaseManager();
            String playerUuid = player.getUuidAsString();
            try{
                if((dbm.playerItemCount(playerUuid, "auctionhouse") + dbm.playerItemCount(playerUuid, "expireditems")) >= ConfigManager.getConfigData(ConfigManager.configFile).getMaxItemsPerPlayer()){
                    context.getSource().sendFeedback(()-> Text.literal("You have too many items on auction").formatted(Formatting.RED), false);
                    return -1;
                }

                if(AuctionHouseMain.ah.canAddItems()){
                    double price = DoubleArgumentType.getDouble(context, "price");
                    ItemStack itemInHand = player.getMainHandStack();
                    String item = Registries.ITEM.getId(itemInHand.getItem()).toString();
                    ConfigData configData = ConfigManager.getConfigData(ConfigManager.configFile);
                    AuctionHouseMain.getDatabaseManager().addItemToAuction(playerUuid, player.getName().getString(), serialize(itemInHand.getComponents()).toString(), item, itemInHand.getCount(), price, configData.getAuctionSecondsDuration());
                    player.getInventory().removeStack(player.getInventory().getSlotWithStack(itemInHand), itemInHand.getCount());
                    context.getSource().sendFeedback(()-> Text.literal(String.format("Item successfully added to auction house for %.2f $", price)).formatted(Formatting.GREEN), false);
                    return 0;
                }

            } catch (UnsupportedEncodingException | FileNotFoundException e){
                LOGGER.error("Failed to add item to auction", e);
            }

            context.getSource().sendFeedback(()-> Text.literal("The auction house is full!").formatted(Formatting.RED), false);

            return -1;
        }
        return -1;
    }
}
