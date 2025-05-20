package co.lemee.auctionhouse;

import co.lemee.auctionhouse.auction.AuctionHouse;
import co.lemee.auctionhouse.auction.ExpiredItems;
import co.lemee.auctionhouse.config.ConfigManager;
import co.lemee.auctionhouse.sql.DatabaseManager;
import co.lemee.auctionhouse.sql.SQLiteDatabaseManager;
import co.lemee.auctionhouse.util.CommonMethods;
import co.lemee.auctionhouse.util.Register;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuctionHouseMod implements ModInitializer {
    public static final String MOD_ID = "auctionhouse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ComponentType<String> SKULL_OWNER = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(MOD_ID, "skullowner"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );
    public static final Connection connection;
    public static AuctionHouse ah;
    public static ExpiredItems ei;
    public static ArrayList<String> tableRegistry = new ArrayList<>();

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

    private static void onServerStopping(MinecraftServer server) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Closing database connection", e);
        }

    }

    @Override
    public void onInitialize() {
        if (!ConfigManager.loadConfig())
            throw new RuntimeException("Could not load config");

        LOGGER.info("Fabric AuctionHouse loaded!");

        tableRegistry.add("CREATE TABLE IF NOT EXISTS auctionhouse (id integer PRIMARY KEY AUTOINCREMENT, playeruuid text NOT NULL, owner text NOT NULL, nbt text NOT NULL, item text NOT NULL, count integer NOT NULL, price double NOT NULL, secondsLeft long NOT NULL);");
        tableRegistry.add("CREATE TABLE IF NOT EXISTS expireditems (id integer PRIMARY KEY, playeruuid text NOT NULL, owner text NOT NULL, nbt text NOT NULL, item text NOT NULL, count integer NOT NULL, price double NOT NULL);");

        ServerLifecycleEvents.SERVER_STARTED.register(AuctionHouseMod::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(AuctionHouseMod::onServerStopping);

        Register.registerCommands();
    }
}