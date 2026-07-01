package pl.fuzjajadrowa.froggy.neoforge;

import pl.fuzjajadrowa.froggy.Froggy;
import net.neoforged.fml.common.Mod;

@Mod(Froggy.MOD_ID)
public final class FroggyNeoForge {
    public FroggyNeoForge() {
        Froggy.init();
    }
}
