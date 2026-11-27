package dev.voidbound.network;

import dev.voidbound.Voidbound;
import dev.voidbound.ability.AbilityDefinition;
import dev.voidbound.ability.server.ServerAbilityManager;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class AbilityNetworking {
    private AbilityNetworking() {
    }

    public static void initialize() {
        PayloadTypeRegistry.serverboundPlay().register(AbilityRequestPayload.TYPE, AbilityRequestPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AbilityRequestPayload.TYPE,
                (payload, context) -> handleRequest(context.player(), payload.action()));
    }

    private static void handleRequest(ServerPlayer player, int action) {
        if (action != AbilityRequestPayload.CYCLE && action != AbilityRequestPayload.ACTIVATE) {
            return;
        }
        if (player.hasDisconnected() || player.isRemoved() || !player.isAlive()
                || player.isSpectator() || player.isSleeping()
                || (player.getMainHandItem().getItem() != Voidbound.VOID_CODEX
                && player.getOffhandItem().getItem() != Voidbound.VOID_CODEX)) {
            return;
        }

        ServerAbilityManager manager = ServerAbilityManager.forServer(player.level().getServer());
        UUID playerId = player.getUUID();
        if (action == AbilityRequestPayload.CYCLE) {
            AbilityDefinition selected = manager.selectNext(playerId);
            player.sendOverlayMessage(Component.translatable("message.voidbound.selected",
                    Component.translatable(selected.translationKey())));
            return;
        }

        AbilityDefinition selected = manager.selectedAbility(playerId);
        switch (manager.cooldownStatus(playerId, selected.id())) {
            case UNKNOWN_ABILITY -> player.sendOverlayMessage(Component.translatable("message.voidbound.unknown"));
            case UNAVAILABLE -> player.sendOverlayMessage(Component.translatable("message.voidbound.unavailable",
                    Component.translatable(selected.translationKey())));
            case ON_COOLDOWN -> {
                long remaining = manager.remainingCooldownTicks(playerId, selected.id());
                long seconds = (remaining + 19L) / 20L;
                player.sendOverlayMessage(Component.translatable("message.voidbound.cooldown",
                        Component.translatable(selected.translationKey()), seconds));
            }
            case READY -> player.sendOverlayMessage(Component.translatable("message.voidbound.not_implemented"));
        }
    }
}