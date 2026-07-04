package pl.fuzjajadrowa.froggy.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import pl.fuzjajadrowa.froggy.Froggy;

public record FroggyCoughSyrupPayload(int entityId, boolean isCorrect) implements CustomPacketPayload {
    public static final Type<FroggyCoughSyrupPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "cough_syrup"));

    public static final StreamCodec<FriendlyByteBuf, FroggyCoughSyrupPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.entityId());
                buf.writeBoolean(payload.isCorrect());
            },
            buf -> new FroggyCoughSyrupPayload(buf.readInt(), buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}