package dev.voidbound.ability;

import java.util.Objects;
import net.minecraft.resources.Identifier;

public record AbilityDefinition(Identifier id, String translationKey, boolean available, int cooldownTicks) {
    public AbilityDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(translationKey, "translationKey");
        if (cooldownTicks < 0) {
            throw new IllegalArgumentException("Cooldown cannot be negative");
        }
    }
}