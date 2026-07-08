package pl.fuzjajadrowa.froggy.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity;

import java.util.function.Supplier;

public record FroggyTamedStatePacket(int entityId, int newState) {
    public static void encode(FroggyTamedStatePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId());
        buf.writeInt(packet.newState());
    }

    public static FroggyTamedStatePacket decode(FriendlyByteBuf buf) {
        return new FroggyTamedStatePacket(buf.readInt(), buf.readInt());
    }

    public static void handle(FroggyTamedStatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                net.minecraft.world.entity.Entity entity = player.level().getEntity(packet.entityId());
                if (entity instanceof FroggyTamedEntity froggy) {
                    if (froggy.getOwnerUUID().isPresent() && froggy.getOwnerUUID().get().equals(player.getUUID())) {
                        froggy.setTamedState(packet.newState());
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}