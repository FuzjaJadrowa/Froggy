package pl.fuzjajadrowa.froggy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import pl.fuzjajadrowa.froggy.block.entity.FroggyTrappedChestBlockEntity;
import pl.fuzjajadrowa.froggy.registry.FroggyBlocks;

public class FroggyChestItemRenderer extends BlockEntityWithoutLevelRenderer {
    private FroggyTrappedChestBlockEntity dummy = null;

    public FroggyChestItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (this.dummy == null) {
            this.dummy = new FroggyTrappedChestBlockEntity(BlockPos.ZERO, FroggyBlocks.FROGGY_TRAPPED_CHEST.get().defaultBlockState());
        }
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(this.dummy, poseStack, buffer, packedLight, packedOverlay);
    }
}