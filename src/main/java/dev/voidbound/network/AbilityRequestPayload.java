package dev.voidbound.network;

import dev.voidbound.Voidbound;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AbilityRequestPayload(int action) implements CustomPacketPayload {
    public static final int CYCLE = 0;
    public static final int ACTIVATE = 1;
    public static final Type<AbilityRequestPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Voidbound.MOD_ID, "ability_request"));
    public static final StreamCodec<ByteBuf, AbilityRequestPayload> CODEC =
            ByteBufCodecs.VAR_INT.map(AbilityRequestPayload::new, AbilityRequestPayload::action);

    @Override
    public Type<AbilityRequestPayload> type() {
        return TYPE;
    }
}