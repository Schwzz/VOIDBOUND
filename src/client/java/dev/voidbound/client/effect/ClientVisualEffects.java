package dev.voidbound.client.effect;

import dev.voidbound.network.VisualEffectPayload;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class ClientVisualEffects {
    private static final Map<Identifier, BiConsumer<Minecraft, VisualEffectPayload>> HANDLERS = new HashMap<>();

    private ClientVisualEffects() {
    }

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(VisualEffectPayload.TYPE, (payload, context) -> {
            BiConsumer<Minecraft, VisualEffectPayload> handler = HANDLERS.get(payload.effectId());
            if (handler != null && context.client().level != null) {
                handler.accept(context.client(), payload);
            }
        });
    }

    public static void register(Identifier effectId, BiConsumer<Minecraft, VisualEffectPayload> handler) {
        Objects.requireNonNull(effectId, "effectId");
        Objects.requireNonNull(handler, "handler");
        if (HANDLERS.putIfAbsent(effectId, handler) != null) {
            throw new IllegalArgumentException("Duplicate visual effect: " + effectId);
        }
    }
}