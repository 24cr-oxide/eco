package com.willfp.eco.core.config.json;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.json.wrapper.LoadableJSONConfigWrapper;
import org.jetbrains.annotations.NotNull;

public abstract class JsonStaticBaseConfig extends LoadableJSONConfigWrapper {
    /**
     * Config implementation for configs present in the plugin's base directory (eg config.json, lang.json).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     */
    protected JsonStaticBaseConfig(@NotNull final String configName,
                                   @NotNull final EcoPlugin plugin) {
        super(Eco.getHandler().getConfigFactory().createLoadableJSONConfig(configName, plugin, "", plugin.getClass()));
    }
}