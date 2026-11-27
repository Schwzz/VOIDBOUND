package dev.voidbound.ability;

import dev.voidbound.Voidbound;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.Identifier;

public final class AbilityRegistry {
    public static final AbilityDefinition VOID_COLLAPSE = unavailable("void_collapse");
    public static final AbilityDefinition PHANTOM_ARSENAL = unavailable("phantom_arsenal");
    public static final AbilityDefinition RIFT_WALK = unavailable("rift_walk");
    public static final AbilityDefinition APOCALYPSE = unavailable("apocalypse");

    private static final List<AbilityDefinition> ABILITIES = List.of(
            VOID_COLLAPSE, PHANTOM_ARSENAL, RIFT_WALK, APOCALYPSE);
    private static final Map<Identifier, AbilityDefinition> BY_ID = Map.of(
            VOID_COLLAPSE.id(), VOID_COLLAPSE,
            PHANTOM_ARSENAL.id(), PHANTOM_ARSENAL,
            RIFT_WALK.id(), RIFT_WALK,
            APOCALYPSE.id(), APOCALYPSE);

    private AbilityRegistry() {
    }

    public static List<AbilityDefinition> all() {
        return ABILITIES;
    }

    public static Optional<AbilityDefinition> find(Identifier id) {
        return Optional.ofNullable(BY_ID.get(Objects.requireNonNull(id, "id")));
    }

    private static AbilityDefinition unavailable(String path) {
        return new AbilityDefinition(
                Identifier.fromNamespaceAndPath(Voidbound.MOD_ID, path),
                "ability." + Voidbound.MOD_ID + "." + path,
                false,
                0);
    }
}