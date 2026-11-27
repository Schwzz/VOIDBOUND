package dev.voidbound.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class VisualEffectNetworking {
    private VisualEffectNetworking() {
    }

    public static void sendToTrackingAndSelf(ServerPlayer caster, VisualEffectPayload payload) {
        for (ServerPlayer viewer : PlayerLookup.tracking(caster)) {
            if (viewer != caster && ServerPlayNetworking.canSend(viewer, VisualEffectPayload.TYPE)) {
                ServerPlayNetworking.send(viewer, payload);
            }
        }
        if (ServerPlayNetworking.canSend(caster, VisualEffectPayload.TYPE)) {
            ServerPlayNetworking.send(caster, payload);
        }
    }
}