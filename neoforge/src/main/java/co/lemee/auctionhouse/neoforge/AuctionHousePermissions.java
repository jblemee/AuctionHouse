package co.lemee.auctionhouse.neoforge;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.command.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import static co.lemee.auctionhouse.AuctionHouseMod.LOGGER;

public class AuctionHousePermissions {

    private static final PermissionNode<Boolean> CANCEL_PERM = new PermissionNode<>(
            AuctionHouseMod.MOD_ID,
            "cancel",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);

    private static final PermissionNode<Boolean> EXPIRED_PERM = new PermissionNode<>(
            AuctionHouseMod.MOD_ID,
            "expired",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);
    private static final PermissionNode<Boolean> HELP_PERM = new PermissionNode<>(
            AuctionHouseMod.MOD_ID,
            "help",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);
    private static final PermissionNode<Boolean> MAIN_PERM = new PermissionNode<>(
            AuctionHouseMod.MOD_ID,
            "main",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);
    private static final PermissionNode<Boolean> RETURN_PERM = new PermissionNode<>(
            AuctionHouseMod.MOD_ID,
            "return",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);
    private static final PermissionNode<Boolean> RELOAD_PERM = new PermissionNode<>(
            AuctionHouseMod.MOD_ID,
            "reload",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> {
                if (player == null) {
                    return false;
                } else {
                    return player.hasPermissions(4);
                }
            });
    private static final PermissionNode<Boolean> SELL_PERM = new PermissionNode<>(
            AuctionHouseMod.MOD_ID,
            "sell",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);

    private static final PermissionNode<Boolean> SELLING_PERM = new PermissionNode<>(
            AuctionHouseMod.MOD_ID,
            "selling",
            PermissionTypes.BOOLEAN,
            (player, uuid, permissionDynamicContexts) -> true);

    public static boolean hasPermission(CommandSourceStack source, PermissionNode<Boolean> permission) {
        try {
            return PermissionAPI.getPermission(source.getPlayerOrException(), permission);
        } catch (CommandSyntaxException e) {
            return false;
        }
    }

    @SubscribeEvent
    public void permission(PermissionGatherEvent.Nodes event) {
        LOGGER.info("Registering permission nodes...");
        event.addNodes(CANCEL_PERM, SELL_PERM, SELLING_PERM, EXPIRED_PERM, HELP_PERM, MAIN_PERM, RETURN_PERM, RELOAD_PERM);
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        this.register(dispatcher);
    }

    private void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("cancel")
                        .requires((cs) -> hasPermission(cs, CANCEL_PERM))
                        .executes(AuctionHouseCancelCommand::run)));
        dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("expired")
                        .requires(cs -> hasPermission(cs, EXPIRED_PERM))
                        .executes(AuctionHouseExpiredCommand::run)));
        dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("help")
                        .requires(cs -> hasPermission(cs, HELP_PERM))
                        .executes(AuctionHouseHelpCommand::run)));
        dispatcher.register(Commands.literal("ah")
                .requires(cs -> hasPermission(cs, MAIN_PERM))
                .executes(AuctionHouseMainCommand::run));
        dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("return")
                        .requires(cs -> hasPermission(cs, RETURN_PERM))
                        .executes(AuctionHouseReturnCommand::run)));
        dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("reload")
                        .requires(cs -> hasPermission(cs, RELOAD_PERM))
                        .executes(AuctionHouseReloadCommand::run)));
        dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("sell")
                        .then(Commands.argument("price", DoubleArgumentType.doubleArg(0))
                                .requires(cs -> hasPermission(cs, SELL_PERM))
                                .executes(AuctionHouseSellCommand::run))));
        dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("selling")
                        .requires(cs -> hasPermission(cs, SELLING_PERM))
                        .executes(AuctionHouseSellingCommand::run)));
    }
}
