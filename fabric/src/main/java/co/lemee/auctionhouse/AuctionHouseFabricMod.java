package co.lemee.auctionhouse;

import co.lemee.auctionhouse.command.*;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;

import static co.lemee.auctionhouse.AuctionHouseMod.LOGGER;

public class AuctionHouseFabricMod implements ModInitializer {

    @Override
    public void onInitialize() {

        AuctionHouseMod.realeconomy = FabricLoader.getInstance().isModLoaded("realeconomy");
        AuctionHouseMod.impactor = FabricLoader.getInstance().isModLoaded("impactor");

        AuctionHouseMod.initialize();

        ServerLifecycleEvents.SERVER_STARTED.register(AuctionHouseMod::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(AuctionHouseMod::onServerStopping);

        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("cancel")
                        .requires(Permissions.require("auctionhouse.cancel", 0))
                        .executes(AuctionHouseCancelCommand::run))));
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("expired")
                        .requires(Permissions.require("auctionhouse.expired", 0))
                        .executes(AuctionHouseExpiredCommand::run))));
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("help")
                        .requires(Permissions.require("auctionhouse.help", 0))
                        .executes(AuctionHouseHelpCommand::run))));
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> dispatcher.register(Commands.literal("ah")
                .requires(Permissions.require("auctionhouse.main", 0))
                .executes(AuctionHouseMainCommand::run)));
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("return")
                        .requires(Permissions.require("auctionhouse.return", 0))
                        .executes(AuctionHouseReturnCommand::run))));
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("reload")
                        .requires(Permissions.require("auctionhouse.reload", 4))
                        .executes(AuctionHouseReloadCommand::run))));
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("sell")
                        .then(Commands.argument("price", DoubleArgumentType.doubleArg(0))
                                .requires(Permissions.require("auctionhouse.sell", 0))
                                .executes(AuctionHouseSellCommand::run)))));
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> dispatcher.register(Commands.literal("ah")
                .then(Commands.literal("selling")
                        .requires(Permissions.require("auctionhouse.selling", 0))
                        .executes(AuctionHouseSellingCommand::run))));

        LOGGER.info("AuctionHouse loaded!");
    }
}