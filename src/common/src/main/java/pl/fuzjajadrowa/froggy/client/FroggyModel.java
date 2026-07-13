package pl.fuzjajadrowa.froggy.client;

import net.minecraft.resources.ResourceLocation;
import pl.fuzjajadrowa.froggy.Froggy;
import pl.fuzjajadrowa.froggy.entity.BaseFroggyEntity;
import software.bernie.geckolib.model.GeoModel;

public class FroggyModel<T extends BaseFroggyEntity> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        if (animatable instanceof pl.fuzjajadrowa.froggy.entity.FroggyTraderEntity) {
            return ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "geo/froggy_trader.geo.json");
        }
        return ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "geo/froggy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        if (animatable instanceof pl.fuzjajadrowa.froggy.entity.FroggyTraderEntity) {
            return ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "textures/entity/froggy_trader.png");
        }
        if (animatable instanceof pl.fuzjajadrowa.froggy.entity.FroggySleepingEntity sleeping && !sleeping.isScreaming() && sleeping.getEffectState() == 0) {
            return ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "textures/entity/froggy_sleeping.png");
        }
        if (animatable instanceof pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity tamed && tamed.isSleepingInBed()) {
            return ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "textures/entity/froggy_sleeping.png");
        }
        return ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "textures/entity/froggy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "animations/froggy.animation.json");
    }
}