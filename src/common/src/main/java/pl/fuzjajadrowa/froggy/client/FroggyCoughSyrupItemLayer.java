package pl.fuzjajadrowa.froggy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import pl.fuzjajadrowa.froggy.entity.BaseFroggyEntity;
import pl.fuzjajadrowa.froggy.registry.FroggyItems;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class FroggyCoughSyrupItemLayer<T extends BaseFroggyEntity> extends BlockAndItemGeoLayer<T> {
    public FroggyCoughSyrupItemLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    protected ItemStack getStackForBone(GeoBone bone, T animatable) {
        if (bone.getName().equals("rai")) {
            if (animatable.getEffectState() == BaseFroggyEntity.STATE_EATING) {
                return new ItemStack(FroggyItems.COUGH_SYRUP.get());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected BlockState getBlockForBone(GeoBone bone, T animatable) {
        return null;
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
        return ItemDisplayContext.FIXED;
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        if (bone.getName().equals("rai")) {
            poseStack.pushPose();
            // Center and scale the syrup item on the rai bone
            poseStack.translate(0.0f, 0.05f, 0.0f);
            poseStack.scale(0.45f, 0.45f, 0.45f);
            super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        }
    }
}
