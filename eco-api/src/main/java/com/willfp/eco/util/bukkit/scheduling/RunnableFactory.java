package com.willfp.eco.util.bukkit.scheduling;

import com.willfp.eco.internal.bukkit.events.EcoRunnableTask;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.EcoPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RunnableFactory extends PluginDependent {
    /**
     * Factory class to produce {@link RunnableTask}s associated with an {@link EcoPlugin}.
     *
     * @param plugin The plugin that this factory creates runnables for.
     */
    public RunnableFactory(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Create a {@link RunnableTask}.
     *
     * @param consumer Lambda of the code to run, where the parameter represents the instance of the runnable.
     * @return The created {@link RunnableTask}.
     */
    public RunnableTask create(@NotNull final Consumer<RunnableTask> consumer) {
        return new EcoRunnableTask(this.getPlugin()) {
            @Override
            public void run() {
                consumer.accept(this);
            }
        };
    }
}
