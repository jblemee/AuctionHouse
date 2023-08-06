package unsafedodo.fabricauctionhouse;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unsafedodo.fabricauctionhouse.auction.AuctionHouse;
import unsafedodo.fabricauctionhouse.auction.ExpiredItems;
import unsafedodo.fabricauctionhouse.config.ConfigManager;
import unsafedodo.fabricauctionhouse.sql.DatabaseManager;
import unsafedodo.fabricauctionhouse.sql.SQLiteDatabaseManager;
import unsafedodo.fabricauctionhouse.util.EconomyTransactionHandler;
import unsafedodo.fabricauctionhouse.util.Register;

import static com.epherical.octoecon.api.event.EconomyEvents.ECONOMY_CHANGE_EVENT;

import java.util.ArrayList;

public class AuctionHouseMain implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("fabric-auctionhouse");
	public static AuctionHouse ah = new AuctionHouse(null);
	public static ExpiredItems ei;

	public static ArrayList<String> tableRegistry = new ArrayList<>();

	public static final EconomyTransactionHandler transactionHandler = new EconomyTransactionHandler();

	public static DatabaseManager getDatabaseManager(){
		return new SQLiteDatabaseManager();
	}

	public static void onServerStarted(MinecraftServer server){
		ah = new AuctionHouse(SQLiteDatabaseManager.getItemList());
		ei = new ExpiredItems(SQLiteDatabaseManager.getExpiredItemsList());
		ECONOMY_CHANGE_EVENT.register(transactionHandler);
	}
	@Override
	public void onInitialize() {
		if(!ConfigManager.loadConfig())
			throw new RuntimeException("Could not load config");

		LOGGER.info("Fabric AuctionHouse loaded!");

		ServerLifecycleEvents.SERVER_STARTED.register(AuctionHouseMain::onServerStarted);
		tableRegistry.add("CREATE TABLE IF NOT EXISTS auctionhouse (id integer PRIMARY KEY AUTOINCREMENT, playeruuid text NOT NULL, owner text NOT NULL, nbt text NOT NULL, item text NOT NULL, count integer NOT NULL, price double NOT NULL, secondsleft long NOT NULL);");
		tableRegistry.add("CREATE TABLE IF NOT EXISTS expireditems (id integer PRIMARY KEY, playeruuid text NOT NULL, owner text NOT NULL, nbt text NOT NULL, item text NOT NULL, count integer NOT NULL, price double NOT NULL);");
		Register.registerCommands();
	}
}