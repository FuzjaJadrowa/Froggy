package pl.fuzjajadrowa.froggy.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import pl.fuzjajadrowa.froggy.Froggy;

public record FroggyTamedStatePayload(int entityId, int newState) implements CustomPacketPayload {
    public static final Type<FroggyTamedStatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "tamed_state"));

    public static final StreamCodec<FriendlyByteBuf, FroggyTamedStatePayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.entityId());
                buf.writeInt(payload.newState());
            },
            buf -> new FroggyTamedStatePayload(buf.readInt(), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}