package io.github.mireole.jeidump.json;

import com.github.bsideup.jabel.Desugar;
import io.github.mireole.jeidump.JEIDump;
import net.minecraft.item.ItemStack;

@Desugar
public record RecipeCatalyst(String name) {
    public static RecipeCatalyst fromJei(Object catalyst) {
        if (catalyst instanceof ItemStack stack) {
            return new RecipeCatalyst(stack.getItem().getRegistryName().toString() + ":" + stack.getMetadata());
        }
        else {
            JEIDump.LOGGER.error("Unknown catalyst type: {}", catalyst);
            return new RecipeCatalyst("unknown");
        }
    }
}
