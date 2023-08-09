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
import unsafedodo.fabricauctionhouse.util.CommonMethods;
import unsafedodo.fabricauctionhouse.util.EconomyTransactionHandler;
import unsafedodo.fabricauctionhouse.util.Register;

import static com.epherical.octoecon.api.event.EconomyEvents.ECONOMY_CHANGE_EVENT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuctionHouseMain implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("fabric-auctionhouse");

	public static AuctionHouse ah;
	public static ExpiredItems ei;
	public static ArrayList<String> tableRegistry = new ArrayList<>();
	public static final EconomyTransactionHandler transactionHandler = new EconomyTransactionHandler();

	public static final Connection connection;

	static {
		try {
			connection = DriverManager.getConnection(SQLiteDatabaseManager.url);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static DatabaseManager getDatabaseManager(){
		return new SQLiteDatabaseManager();
	}


	public static void onServerStarted(MinecraftServer server) {
		SQLiteDatabaseManager.createTables(tableRegistry);
		CommonMethods.reloadHouse();
		CommonMethods.reloadExpired();
	}

	@Override
	public void onInitialize() {
		if(!ConfigManager.loadConfig())
			throw new RuntimeException("Could not load config");



		LOGGER.info("Fabric AuctionHouse loaded!");

		tableRegistry.add("CREATE TABLE IF NOT EXISTS auctionhouse (id integer PRIMARY KEY AUTOINCREMENT, playeruuid text NOT NULL, owner text NOT NULL, nbt text NOT NULL, item text NOT NULL, count integer NOT NULL, price double NOT NULL, secondsLeft long NOT NULL);");
		tableRegistry.add("CREATE TABLE IF NOT EXISTS expireditems (id integer PRIMARY KEY, playeruuid text NOT NULL, owner text NOT NULL, nbt text NOT NULL, item text NOT NULL, count integer NOT NULL, price double NOT NULL);");

		ServerLifecycleEvents.SERVER_STARTED.register(AuctionHouseMain::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPING.register(AuctionHouseMain::onServerStopping);

		Register.registerCommands();

		ECONOMY_CHANGE_EVENT.register(currentEconomy -> {
			transactionHandler.onEconomyChanged(currentEconomy);
		});
	}

	private static void onServerStopping(MinecraftServer server) {
		try {
			connection.close();
		} catch (SQLException e){
			e.printStackTrace();
		}

	}


}