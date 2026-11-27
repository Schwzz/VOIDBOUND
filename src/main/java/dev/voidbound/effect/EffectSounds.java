package dev.voidbound.effect;

import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public final class EffectSounds {
    private EffectSounds() {
    }

    public static void play(ServerLevel level, Vec3 position, SoundEvent sound,
            SoundSource source, float volume, float pitch) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(sound, "sound");
        Objects.requireNonNull(source, "source");
        if (!Float.isFinite(volume) || volume < 0.0F || !Float.isFinite(pitch) || pitch <= 0.0F) {
            throw new IllegalArgumentException("Invalid sound volume or pitch");
        }
        level.playSound(null, position.x, position.y, position.z, sound, source, volume, pitch);
    }

    public static void cue(Timeline timeline, long tick, ServerLevel level, Vec3 position,
            SoundEvent sound, SoundSource source, float volume, float pitch) {
        timeline.cue(tick, () -> play(level, position, sound, source, volume, pitch));
    }
}