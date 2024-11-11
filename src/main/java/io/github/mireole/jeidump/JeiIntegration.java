package io.github.mireole.jeidump;

import mezz.jei.api.*;
import org.jetbrains.annotations.NotNull;

@JEIPlugin
public class JeiIntegration implements IModPlugin {
    public static IRecipeRegistry registry;
    public static IRecipesGui recipesGui;

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        registry = jeiRuntime.getRecipeRegistry();
        recipesGui = jeiRuntime.getRecipesGui();
    }
}
