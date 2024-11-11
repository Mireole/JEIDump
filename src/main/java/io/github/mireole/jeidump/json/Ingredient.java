package io.github.mireole.jeidump.json;

import com.github.bsideup.jabel.Desugar;
import io.github.mireole.jeidump.DumpCommand;
import io.github.mireole.jeidump.JEIDump;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@Desugar
public record Ingredient(String type, String name, int count) {
    public static <T> Ingredient fromJei(IIngredientType<T> type, T object) {
        if (type == null || object == null) {
            return null;
        }
        if (type == VanillaTypes.ITEM) {
            ItemStack stack = (ItemStack) object;
            String name = stack.getItem().getRegistryName().toString() + ":" + stack.getMetadata();
            DumpCommand.TRANSLATION_MAP.put(name, stack.getDisplayName());
            return new Ingredient("item", name, stack.getCount());
        } else if (type == VanillaTypes.FLUID) {
            FluidStack stack = (FluidStack) object;
            DumpCommand.TRANSLATION_MAP.put(stack.getFluid().getName(), stack.getLocalizedName());
            return new Ingredient("fluid", stack.getFluid().getName(), stack.amount);
        } else {
            JEIDump.LOGGER.error("Unknown ingredient type: {}", object);
            return new Ingredient("unknown", object.toString(), 0);
        }
    }
}
