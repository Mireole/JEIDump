package io.github.mireole.jeidump;

import com.google.gson.Gson;
import io.github.mireole.jeidump.json.RecipeCategory;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DumpCommand extends CommandTreeBase {
    public static final Map<String, String> TRANSLATION_MAP = new HashMap<>();

    @NotNull
    @Override
    public String getName() {
        return "jeidump";
    }

    @NotNull
    @Override
    public String getUsage(@NotNull ICommandSender sender) {
        return "jeidump";
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender,
                        String @NotNull [] args) {

        if (dump()) sender.sendMessage(new TextComponentString("Dumped recipes to jeidump.json"));
        else sender.sendMessage(new TextComponentString("Error while dumping"));
    }

    public static boolean dump() {
        IRecipeRegistry registry = JeiIntegration.registry;
        if (registry == null) {
            return false;
        }

        List<IRecipeCategory> jeiCategories = registry.getRecipeCategories();
        List<RecipeCategory> categories = new ArrayList<>();
        jeiCategories.forEach(category -> categories.add(RecipeCategory.fromJei(category, registry)));

        Gson gson = new Gson();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("jeidump.json"));
            gson.toJson(categories, writer);
            writer.close();
        } catch (IOException e) {
            JEIDump.LOGGER.error("Error writing to file", e);
            return false;
        }

        dumpTranslation();

        return true;
    }

    public static void dumpTranslation() {
        Gson gson = new Gson();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("translations.json"));

            gson.toJson(TRANSLATION_MAP, writer);
            writer.close();
        } catch (IOException e) {
            JEIDump.LOGGER.error("Error writing to file", e);
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 1;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
