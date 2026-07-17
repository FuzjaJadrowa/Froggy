package pl.fuzjajadrowa.froggy.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import pl.fuzjajadrowa.froggy.world.FroggyWorldData;

public class FroggyCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("froggy")
                .then(Commands.literal("locatehouse")
                    .requires(source -> source.hasPermission(2))
                    .executes(context -> {
                        CommandSourceStack source = context.getSource();
                        FroggyWorldData data = FroggyWorldData.get(source.getLevel());
                        if (data.isGenerated()) {
                            source.sendSuccess(() -> Component.literal("Froggy House is located at: " + data.getHouseX() + " " + data.getHouseY() + " " + data.getHouseZ()), false);
                            return 1;
                        } else {
                            source.sendFailure(Component.literal("Froggy House has not been generated in this world yet."));
                            return 0;
                        }
                    }))
        );
    }
}