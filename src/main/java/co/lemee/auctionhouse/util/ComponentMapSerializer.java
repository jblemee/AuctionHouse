package co.lemee.auctionhouse.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.component.ComponentMap;

import static co.lemee.auctionhouse.AuctionHouseMod.LOGGER;

public class ComponentMapSerializer {

    public static ComponentMap deserialize(JsonElement jsonElement) throws JsonParseException {
        return ComponentMap.CODEC.parse(JsonOps.INSTANCE, jsonElement).ifError(error -> LOGGER.error("Failed to deserialize ComponentMap: {}", error))
                .result()
                .orElseThrow();
    }

    public static JsonElement serialize(ComponentMap stack) {
        return ComponentMap.CODEC.encodeStart(JsonOps.INSTANCE, stack)
                .ifError(error -> LOGGER.error("Failed to serialize ComponentMap: {}", error))
                .result()
                .orElseThrow();
    }
}
