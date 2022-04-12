package com.redlimerl.mcsr.statsperworld.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Shadow public Session session;

    @Shadow public File runDirectory;

    @Shadow public StatHandler statHandler;
    private static StatHandler STATS_PER_WORLD_STAT_HANDLER = null;

    @Inject(method = "initializeGame", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        STATS_PER_WORLD_STAT_HANDLER = this.statHandler;
    }

    @Inject(method = "method_2935",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;connect(Lnet/minecraft/client/world/ClientWorld;)V", shift = At.Shift.AFTER))
    public void onStartedServer(String string, String string2, LevelInfo levelInfo, CallbackInfo ci) {
        File stats = this.runDirectory.toPath().resolve("saves").resolve(string).toFile();
        if (!stats.exists()) stats.mkdirs();
        this.statHandler = new StatHandler(this.session, stats);
    }

    @Inject(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At("HEAD"))
    public void onConnect(ClientWorld world, String loadingMessage, CallbackInfo ci) {
        if (world == null && this.statHandler != STATS_PER_WORLD_STAT_HANDLER) {
            this.statHandler.method_1739();
            this.statHandler = STATS_PER_WORLD_STAT_HANDLER;
        }
    }
}
