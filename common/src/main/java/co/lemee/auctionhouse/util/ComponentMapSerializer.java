package co.lemee.auctionhouse.util;

import co.lemee.auctionhouse.AuctionHouseMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.RegistryOps;

import static co.lemee.auctionhouse.AuctionHouseMod.LOGGER;

public class ComponentMapSerializer {

    private static RegistryOps<JsonElement> getRegistryOps() {
        return RegistryOps.create(JsonOps.INSTANCE, AuctionHouseMod.server.registryAccess());
    }

    public static DataComponentMap deserialize(JsonElement jsonElement) throws JsonParseException {
        return DataComponentMap.CODEC.parse(getRegistryOps(), jsonElement).ifError(error -> LOGGER.error("Failed to deserialize ComponentMap: {}", error))
                .result()
                .orElseThrow();
    }

    public static JsonElement serialize(DataComponentMap stack) {
        return DataComponentMap.CODEC.encodeStart(getRegistryOps(), stack)
                .ifError(error -> LOGGER.error("Failed to serialize ComponentMap: {}", error))
                .result()
                .orElseThrow();
    }
}
