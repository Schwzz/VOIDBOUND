package dev.voidbound.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.voidbound.Voidbound;
import dev.voidbound.network.AbilityRequestPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class VoidboundControls {
    private static KeyMapping cycle;
    private static KeyMapping activate;

    private VoidboundControls() {
    }

    public static void initialize() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(Voidbound.MOD_ID, "controls"));
        cycle = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.voidbound.cycle_ability",
                InputConstants.Type.KEYBOARD, InputConstants.KEY_Z, category));
        activate = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.voidbound.activate_ability",
                InputConstants.Type.KEYBOARD, InputConstants.KEY_R, category));
        ClientTickEvents.END_CLIENT_TICK.register(VoidboundControls::tick);
    }

    private static void tick(Minecraft client) {
        boolean allowed = client.player != null && client.gui.screen() == null
                && !client.options.keyDebugModifier.isDown()
                && (client.player.getMainHandItem().getItem() == Voidbound.VOID_CODEX
                || client.player.getOffhandItem().getItem() == Voidbound.VOID_CODEX)
                && ClientPlayNetworking.canSend(AbilityRequestPayload.TYPE);
        while (cycle.consumeClick()) {
            if (allowed) {
                ClientPlayNetworking.send(new AbilityRequestPayload(AbilityRequestPayload.CYCLE));
            }
        }
        while (activate.consumeClick()) {
            if (allowed) {
                ClientPlayNetworking.send(new AbilityRequestPayload(AbilityRequestPayload.ACTIVATE));
            }
        }
    }

    public static void discardDebugClick(InputConstants.Key key) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.options == null || !client.options.keyDebugModifier.isDown()) {
            return;
        }
        if (cycle != null && KeyMappingHelper.getBoundKeyOf(cycle).equals(key)) {
            cycle.consumeClick();
        }
        if (activate != null && KeyMappingHelper.getBoundKeyOf(activate).equals(key)) {
            activate.consumeClick();
        }
    }
}