package co.lemee.auctionhouse.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponentMap;

import static co.lemee.auctionhouse.AuctionHouseMod.LOGGER;

public class ComponentMapSerializer {

    public static DataComponentMap deserialize(JsonElement jsonElement) throws JsonParseException {
        return DataComponentMap.CODEC.parse(JsonOps.INSTANCE, jsonElement).ifError(error -> LOGGER.error("Failed to deserialize ComponentMap: {}", error))
                .result()
                .orElseThrow();
    }

    public static JsonElement serialize(DataComponentMap stack) {
        return DataComponentMap.CODEC.encodeStart(JsonOps.INSTANCE, stack)
                .ifError(error -> LOGGER.error("Failed to serialize ComponentMap: {}", error))
                .result()
                .orElseThrow();
    }
}
