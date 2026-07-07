package pl.fuzjajadrowa.froggy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pl.fuzjajadrowa.froggy.entity.FroggySleepingEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FroggySleepingRenderer extends GeoEntityRenderer<FroggySleepingEntity> {
    public FroggySleepingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FroggyModel<>());
        this.addRenderLayer(new FroggyCoughSyrupItemLayer<>(this));
    }

//? if >=1.21.1 {
    @Override
    protected void applyRotations(FroggySleepingEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
//?} else {
/*    @Override
    protected void applyRotations(FroggySleepingEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick);
*/
//?}

        if (!entity.isScreaming() && entity.getEffectState() == 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(0.0, -0.6, -0.2);
        }
    }
}