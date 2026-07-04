package pl.fuzjajadrowa.froggy.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import pl.fuzjajadrowa.froggy.entity.BaseFroggyEntity;

import java.util.function.Supplier;

public record FroggyCoughSyrupPacket(int entityId, boolean isCorrect) {
    public static void encode(FroggyCoughSyrupPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId());
        buf.writeBoolean(packet.isCorrect());
    }

    public static FroggyCoughSyrupPacket decode(FriendlyByteBuf buf) {
        return new FroggyCoughSyrupPacket(buf.readInt(), buf.readBoolean());
    }

    public static void handle(FroggyCoughSyrupPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                BaseFroggyEntity.handleCoughSyrupChoice(player, packet.entityId(), packet.isCorrect());
            }
        });
        context.setPacketHandled(true);
    }
}
