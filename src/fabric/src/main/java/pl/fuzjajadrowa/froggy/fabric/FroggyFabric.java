package pl.fuzjajadrowa.froggy.fabric;

import pl.fuzjajadrowa.froggy.Froggy;
import net.fabricmc.api.ModInitializer;

public final class FroggyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Froggy.init();
    }
}
