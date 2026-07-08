package pl.fuzjajadrowa.froggy.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import pl.fuzjajadrowa.froggy.Froggy;

public class FroggyPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Froggy.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, FroggyCoughSyrupPacket.class,
                FroggyCoughSyrupPacket::encode,
                FroggyCoughSyrupPacket::decode,
                FroggyCoughSyrupPacket::handle
        );
        INSTANCE.registerMessage(id++, FroggyTamedStatePacket.class,
                FroggyTamedStatePacket::encode,
                FroggyTamedStatePacket::decode,
                FroggyTamedStatePacket::handle
        );
    }
}
