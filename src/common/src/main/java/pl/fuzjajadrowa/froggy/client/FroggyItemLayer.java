package pl.fuzjajadrowa.froggy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import pl.fuzjajadrowa.froggy.entity.BaseFroggyEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class FroggyItemLayer<T extends BaseFroggyEntity> extends BlockAndItemGeoLayer<T> {
    public FroggyItemLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    protected ItemStack getStackForBone(GeoBone bone, T animatable) {
        if (bone.getName().equals("rai")) {
            int state = animatable.getEffectState();
            if (state == BaseFroggyEntity.STATE_EATING || state == BaseFroggyEntity.STATE_EATING_FOOD) {
                return animatable.getEatenItem();
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
            poseStack.translate(0.0f, 0.05f, 0.0f);
            poseStack.scale(0.33f, 0.33f, 0.33f);
            super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        }
    }
}