package com.redlimerl.mcsr.statsperworld.mixin;

import com.redlimerl.mcsr.statsperworld.StatsPerWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Final @Shadow private Session session;

    @Final @Shadow public File runDirectory;

    @Shadow public StatHandler field_3763;
    public static StatHandler STATS_PER_WORLD_STAT_HANDLER = null;

    @Inject(method = "initializeGame", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        STATS_PER_WORLD_STAT_HANDLER = this.field_3763;
    }

    @Inject(method = "method_2935",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;connect(Lnet/minecraft/client/world/ClientWorld;)V", shift = At.Shift.AFTER))
    public void onStartedServer(String string, String string2, LevelInfo levelInfo, CallbackInfo ci) {
        File stats = this.runDirectory.toPath().resolve("saves").resolve(string).toFile();
        stats.mkdirs();
        this.field_3763 = new StatHandler(this.session, stats);
    }

    @Inject(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At("HEAD"))
    public void onConnect(ClientWorld world, String loadingMessage, CallbackInfo ci) {
        if (world == null && this.field_3763 != STATS_PER_WORLD_STAT_HANDLER) {
            this.field_3763.method_1739();
            this.field_3763 = STATS_PER_WORLD_STAT_HANDLER;
        }
    }
}
