package co.lemee.auctionhouse.util;

import co.lemee.auctionhouse.command.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Register {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(AuctionHouseCancelCommand::register);
        CommandRegistrationCallback.EVENT.register(AuctionHouseExpiredCommand::register);
        CommandRegistrationCallback.EVENT.register(AuctionHouseHelpCommand::register);
        CommandRegistrationCallback.EVENT.register(AuctionHouseMainCommand::register);
        CommandRegistrationCallback.EVENT.register(AuctionHouseReturnCommand::register);
        CommandRegistrationCallback.EVENT.register(AuctionHouseReloadCommand::register);
        CommandRegistrationCallback.EVENT.register(AuctionHouseSellCommand::register);
        CommandRegistrationCallback.EVENT.register(AuctionHouseSellingCommand::register);
    }
}
