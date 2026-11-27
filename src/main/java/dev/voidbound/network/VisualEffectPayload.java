package dev.voidbound.network;

import dev.voidbound.Voidbound;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public record VisualEffectPayload(Identifier effectId, Vec3 origin, long seed) implements CustomPacketPayload {
    public static final Type<VisualEffectPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Voidbound.MOD_ID, "visual_effect"));
    public static final StreamCodec<ByteBuf, VisualEffectPayload> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, VisualEffectPayload::effectId,
            Vec3.STREAM_CODEC, VisualEffectPayload::origin,
            ByteBufCodecs.LONG, VisualEffectPayload::seed,
            VisualEffectPayload::new);

    public VisualEffectPayload {
        Objects.requireNonNull(effectId, "effectId");
        Objects.requireNonNull(origin, "origin");
        if (!Double.isFinite(origin.x) || !Double.isFinite(origin.y) || !Double.isFinite(origin.z)) {
            throw new IllegalArgumentException("Effect origin must be finite");
        }
    }

    @Override
    public Type<VisualEffectPayload> type() {
        return TYPE;
    }
}