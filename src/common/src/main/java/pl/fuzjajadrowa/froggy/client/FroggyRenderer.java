package pl.fuzjajadrowa.froggy.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pl.fuzjajadrowa.froggy.entity.BaseFroggyEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FroggyRenderer<T extends BaseFroggyEntity> extends GeoEntityRenderer<T> {
    public FroggyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FroggyModel<>());
    }
}