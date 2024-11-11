package io.github.mireole.jeidump.json;

import com.github.bsideup.jabel.Desugar;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.ingredients.Ingredients;

import java.util.ArrayList;
import java.util.List;

@Desugar
public record Recipe(List<Ingredient> inputs, List<Ingredient> outputs) {
    public static Recipe fromJei(IRecipeWrapper wrapper) {
        Ingredients ingredients = new Ingredients();
        wrapper.getIngredients(ingredients);
        // Inputs
        List<Ingredient> inputs = new ArrayList<>();
        ingredients.getInputIngredients().forEach(
            (type, list) -> list.forEach(
                    (o -> inputs.add(Ingredient.fromJei(type, o)))
            )
        );
        // Outputs
        List<Ingredient> outputs = new ArrayList<>();
        ingredients.getOutputIngredients().forEach(
            (type, list) -> list.forEach(
                    (o -> outputs.add(Ingredient.fromJei(type, o)))
            )
        );
        return new Recipe(inputs, outputs);
    }
}
