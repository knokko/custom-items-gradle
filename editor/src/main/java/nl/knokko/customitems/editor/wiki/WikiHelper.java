package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.recipe.ingredient.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

public class WikiHelper {

    private static final String WIKI_RESOURCE_PREFIX = "nl/knokko/customitems/editor/wiki/";

    static void copyResource(String sourcePath, File destination) throws IOException {
        InputStream resourceInput = Objects.requireNonNull(WikiGenerator.class.getClassLoader().getResourceAsStream(
                WIKI_RESOURCE_PREFIX + sourcePath
        ));
        OutputStream resourceOutput = Files.newOutputStream(destination.toPath());

        copyInputToOutput(resourceInput, resourceOutput);

        resourceInput.close();
        resourceOutput.close();
    }

    public static void copyInputToOutput(InputStream input, OutputStream output) throws IOException {
        byte[] resourceBuffer = new byte[10_000];
        while (true) {
            int numReadBytes = input.read(resourceBuffer);
            if (numReadBytes == -1) break;
            output.write(resourceBuffer, 0, numReadBytes);
        }
        output.flush();
    }

    public static void generateHtml(File file, String cssPath, String title, HtmlGeneratorFunction generator) throws IOException {
        PrintWriter output = new PrintWriter(Files.newOutputStream(file.toPath()));
        output.println("<!DOCTYPE html>");
        output.println("<html>");
        output.println("\t<head>");
        output.println("\t\t<meta charset=\"UTF-8\" />");
        output.println("\t\t<link rel=\"stylesheet\" href=\"" + cssPath + "\" />");
        output.println("\t\t<title>" + title + "</title>");
        output.println("\t</head>");
        output.println("\t<body>");

        generator.generate(output);

        output.println("\t</body>");
        output.println("</html>");
        output.flush();
        output.close();
    }

    @FunctionalInterface
    public interface HtmlGeneratorFunction {
        void generate(PrintWriter output) throws IOException;
    }

    public static String stripColorCodes(String original) {
        StringBuilder uncolored = new StringBuilder();
        int[] originalChars = original.codePoints().toArray();
        for (int index = 0; index < originalChars.length; index++) {
            // 167 is the code of the color character
            if (originalChars[index] == 167) {
                index++;
            } else {
                uncolored.append(new String(originalChars, index, 1));
            }
        }
        return uncolored.toString();
    }

    public static String createTextBasedIngredientHtml(IngredientValues ingredient, String pathRoToot) {
        if (ingredient instanceof CustomItemIngredientValues) {
            CustomItemValues item = ((CustomItemIngredientValues) ingredient).getItem();
            return "<a href=\"" + pathRoToot + "/items/" + item.getName() + ".html\">" + stripColorCodes(item.getDisplayName()) +
                    "</a> x " + ingredient.getAmount();
        } else if (ingredient instanceof SimpleVanillaIngredientValues) {
            return ((SimpleVanillaIngredientValues) ingredient).getMaterial() + " x " + ingredient.getAmount();
        } else if (ingredient instanceof DataVanillaIngredientValues) {
            DataVanillaIngredientValues dataIngredient = (DataVanillaIngredientValues) ingredient;
            return dataIngredient.getMaterial() + " with datavalue " + dataIngredient.getDataValue() + " x " + ingredient.getAmount();
        } else if (ingredient instanceof MimicIngredientValues) {
            return ((MimicIngredientValues) ingredient).getItemId() + " x " + ingredient.getAmount();
        } else if (ingredient instanceof ItemBridgeIngredientValues) {
            return ((ItemBridgeIngredientValues) ingredient).getItemId() + " x " + ingredient.getAmount();
        } else if (ingredient instanceof NoIngredientValues) {
            return "nothing";
        } else {
            throw new IllegalArgumentException("Unknown ingredient class: " + ingredient.getClass());
        }
    }

    public static String describePotionEffect(PotionEffectValues effect) {
        return NameHelper.getNiceEnumName(effect.getType().name()) + " " + effect.getLevel() + " for " + effect.getDuration() + " ticks";
    }

    public static String describePotionEffect(ChancePotionEffectValues effect) {
        return describePotionEffect(PotionEffectValues.createQuick(effect.getType(), effect.getDuration(), effect.getLevel()));
    }

    public static String getDisplayName(CustomContainerValues container) {
        String niceDisplayName = stripColorCodes(container.getSelectionIcon().getDisplayName());
        if (niceDisplayName.isEmpty()) return container.getName();
        else return niceDisplayName;
    }
}
