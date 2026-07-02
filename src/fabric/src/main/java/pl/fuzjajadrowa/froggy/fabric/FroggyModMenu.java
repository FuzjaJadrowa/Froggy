package pl.fuzjajadrowa.froggy.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import pl.fuzjajadrowa.froggy.client.FroggyConfigScreen;

public class FroggyModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new FroggyConfigScreen(parent);
    }
}