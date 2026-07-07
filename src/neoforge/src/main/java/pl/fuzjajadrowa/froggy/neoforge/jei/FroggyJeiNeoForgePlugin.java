package pl.fuzjajadrowa.froggy.neoforge.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import pl.fuzjajadrowa.froggy.Froggy;
import pl.fuzjajadrowa.froggy.registry.FroggyItems;

@JeiPlugin
public class FroggyJeiNeoForgePlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(
            new ItemStack(FroggyItems.FLY_IN_A_BOTTLE.get()),
            VanillaTypes.ITEM_STACK,
            Component.translatable("jei.froggy.fly_in_a_bottle.info")
        );
    }
}