package dev.voidbound.ability.server;

import dev.voidbound.ability.AbilityDefinition;
import dev.voidbound.ability.AbilityRegistry;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

public final class ServerAbilityManager {
    private static final Map<MinecraftServer, ServerAbilityManager> SERVERS = new IdentityHashMap<>();
    private static final AbilityDefinition DEFAULT_ABILITY = AbilityRegistry.VOID_COLLAPSE;

    private final MinecraftServer server;
    private final Map<UUID, PlayerAbilityState> players = new HashMap<>();

    private ServerAbilityManager(MinecraftServer server) {
        this.server = server;
    }

    public static void initialize() {
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVERS.remove(server));
    }

    public static ServerAbilityManager forServer(MinecraftServer server) {
        return SERVERS.computeIfAbsent(Objects.requireNonNull(server, "server"), ServerAbilityManager::new);
    }

    public AbilityDefinition selectedAbility(UUID playerId) {
        PlayerAbilityState state = players.get(Objects.requireNonNull(playerId, "playerId"));
        return state == null ? DEFAULT_ABILITY : state.selected;
    }

    public boolean select(UUID playerId, Identifier abilityId) {
        Objects.requireNonNull(playerId, "playerId");
        AbilityDefinition ability = AbilityRegistry.find(abilityId).orElse(null);
        if (ability == null) {
            return false;
        }
        playerState(playerId).selected = ability;
        return true;
    }

    public AbilityDefinition selectNext(UUID playerId) {
        List<AbilityDefinition> abilities = AbilityRegistry.all();
        int next = (abilities.indexOf(selectedAbility(playerId)) + 1) % abilities.size();
        AbilityDefinition selected = abilities.get(next);
        playerState(playerId).selected = selected;
        return selected;
    }

    public long remainingCooldownTicks(UUID playerId, Identifier abilityId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(abilityId, "abilityId");
        PlayerAbilityState state = players.get(playerId);
        if (state == null) {
            return 0L;
        }
        Long expiry = state.cooldowns.get(abilityId);
        if (expiry == null) {
            return 0L;
        }
        long remaining = expiry - server.overworld().getGameTime();
        if (remaining <= 0L) {
            state.cooldowns.remove(abilityId);
            return 0L;
        }
        return remaining;
    }

    public CooldownStatus cooldownStatus(UUID playerId, Identifier abilityId) {
        Objects.requireNonNull(playerId, "playerId");
        AbilityDefinition ability = AbilityRegistry.find(abilityId).orElse(null);
        if (ability == null) {
            return CooldownStatus.UNKNOWN_ABILITY;
        }
        if (!ability.available()) {
            return CooldownStatus.UNAVAILABLE;
        }
        return remainingCooldownTicks(playerId, abilityId) > 0L
                ? CooldownStatus.ON_COOLDOWN : CooldownStatus.READY;
    }

    public boolean recordSuccessfulActivation(UUID playerId, Identifier abilityId) {
        Objects.requireNonNull(playerId, "playerId");
        AbilityDefinition ability = AbilityRegistry.find(abilityId).orElse(null);
        if (ability == null || !ability.available() || remainingCooldownTicks(playerId, abilityId) > 0L) {
            return false;
        }
        if (ability.cooldownTicks() > 0) {
            playerState(playerId).cooldowns.put(
                    ability.id(), server.overworld().getGameTime() + ability.cooldownTicks());
        }
        return true;
    }

    private PlayerAbilityState playerState(UUID playerId) {
        return players.computeIfAbsent(playerId, ignored -> new PlayerAbilityState());
    }

    public enum CooldownStatus {
        UNKNOWN_ABILITY,
        UNAVAILABLE,
        ON_COOLDOWN,
        READY
    }

    private static final class PlayerAbilityState {
        private AbilityDefinition selected = DEFAULT_ABILITY;
        private final Map<Identifier, Long> cooldowns = new HashMap<>();
    }
}