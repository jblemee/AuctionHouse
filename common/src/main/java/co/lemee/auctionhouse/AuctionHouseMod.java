package co.lemee.auctionhouse;

import co.lemee.auctionhouse.auction.ExpiredItems;
import co.lemee.auctionhouse.config.ConfigManager;
import co.lemee.auctionhouse.sql.DatabaseManager;
import co.lemee.auctionhouse.sql.SQLiteDatabaseManager;
import co.lemee.auctionhouse.util.CommonMethods;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class AuctionHouseMod {
    public static final String MOD_ID = "auctionhouse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Connection connection;
    public static co.lemee.auctionhouse.auction.AuctionHouse ah;
    public static ExpiredItems ei;
    public static ArrayList<String> tableRegistry = new ArrayList<>();
    public static boolean impactor = false;
    public static boolean realeconomy = false;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLiteDatabaseManager.url);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static DatabaseManager getDatabaseManager() {
        return new SQLiteDatabaseManager();
    }

    public static void onServerStarted(MinecraftServer server) {
        SQLiteDatabaseManager.createTables(tableRegistry);
        CommonMethods.reloadHouse();
        CommonMethods.reloadExpired();
    }

    public static void onServerStopping(MinecraftServer server) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Closing database connection", e);
        }

    }

    public static void initialize() {
        if (!ConfigManager.loadConfig())
            throw new RuntimeException("Could not load config");

        LOGGER.info("AuctionHouse loaded!");

        tableRegistry.add("CREATE TABLE IF NOT EXISTS auctionhouse (id integer PRIMARY KEY AUTOINCREMENT, playeruuid text NOT NULL, owner text NOT NULL, nbt text NOT NULL, item text NOT NULL, count integer NOT NULL, price double NOT NULL, secondsLeft long NOT NULL);");
        tableRegistry.add("CREATE TABLE IF NOT EXISTS expireditems (id integer PRIMARY KEY, playeruuid text NOT NULL, owner text NOT NULL, nbt text NOT NULL, item text NOT NULL, count integer NOT NULL, price double NOT NULL);");
    }
}