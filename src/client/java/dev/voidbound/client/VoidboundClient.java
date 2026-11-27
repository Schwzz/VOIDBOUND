package dev.voidbound.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;

public final class VoidboundClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VoidboundControls.initialize();
        LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, renderer, layers, context) -> {
            if (renderer instanceof AvatarRenderer<?> avatarRenderer) {
                layers.register(new VoidCodexLayer(avatarRenderer, context));
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(VoidCodexParticles::tick);
    }
}