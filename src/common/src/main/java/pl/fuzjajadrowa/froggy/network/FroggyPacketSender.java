package pl.fuzjajadrowa.froggy.network;

import java.util.function.BiConsumer;

public class FroggyPacketSender {
    public static BiConsumer<Integer, Boolean> sender;
    public static BiConsumer<Integer, Integer> stateSender;

    public static void sendCoughSyrupChoice(int entityId, boolean isCorrect) {
        if (sender != null) {
            sender.accept(entityId, isCorrect);
        }
    }

    public static void sendTamedStateChange(int entityId, int state) {
        if (stateSender != null) {
            stateSender.accept(entityId, state);
        }
    }
}