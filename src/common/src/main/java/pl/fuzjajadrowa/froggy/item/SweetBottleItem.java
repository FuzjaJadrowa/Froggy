package pl.fuzjajadrowa.froggy.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import pl.fuzjajadrowa.froggy.registry.FroggyItems;
import pl.fuzjajadrowa.froggy.registry.FroggySounds;

public class SweetBottleItem extends Item {
    public SweetBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockPos pos = player.blockPosition();

        if (level.canSeeSky(pos)) {
            level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                FroggySounds.BUZZ.get(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F
            );
            level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                net.minecraft.sounds.SoundEvents.BOTTLE_FILL,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
            );

            if (!level.isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (level.getServer() != null) {
                        level.getServer().execute(() -> {
                            serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(
                                    ResourceLocation.fromNamespaceAndPath(pl.fuzjajadrowa.froggy.Froggy.MOD_ID, "buzz"),
                                    SoundSource.PLAYERS
                            ));
                        });
                    }
                });
                thread.start();
            }

            if (level.isClientSide) {
                return InteractionResultHolder.success(itemStack);
            }

            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            ItemStack flyStack = new ItemStack(FroggyItems.FLY_IN_A_BOTTLE.get());
            if (itemStack.isEmpty()) {
                return InteractionResultHolder.consume(flyStack);
            } else {
                if (!player.getInventory().add(flyStack)) {
                    player.drop(flyStack, false);
                }
                return InteractionResultHolder.consume(itemStack);
            }
        }

        return InteractionResultHolder.pass(itemStack);
    }
}