package io.github.mireole.jeidump.json;

import com.github.bsideup.jabel.Desugar;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

import java.util.ArrayList;
import java.util.List;

@Desugar
public record RecipeCategory(String id, List<RecipeCatalyst> catalysts, List<Recipe> recipes) {
    public static RecipeCategory fromJei(IRecipeCategory<? extends IRecipeWrapper> category, IRecipeRegistry registry) {
        // Catalysts
        List<RecipeCatalyst> catalysts = new ArrayList<>();
        registry.getRecipeCatalysts(category).forEach(catalyst -> catalysts.add(RecipeCatalyst.fromJei(catalyst)));
        // Recipes
        List<Recipe> recipes = new ArrayList<>();
        registry.getRecipeWrappers(category).forEach(recipe -> recipes.add(Recipe.fromJei(recipe)));
        // ID
        String id = category.getUid();
        return new RecipeCategory(id, catalysts, recipes);
    }
}
