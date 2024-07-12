package dev.ftb.mods.ftblh.kubejs;

import dev.ftb.mods.ftblh.LittleHelperBindings;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class KubeJSIntegration extends KubeJSPlugin {
    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("FTBLittleHelper", LittleHelperBindings.INSTANCE);
    }
}
