package co.lemee.auctionhouse.mixin;

import co.lemee.auctionhouse.AuctionHouseMod;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        AuctionHouseMod.ah.tick();
    }
}
