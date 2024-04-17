package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.effect.ChancePotionEffect;
import nl.knokko.customitems.effect.KciPotionEffect;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.sound.KciSound;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

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
        PrintWriter output = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8));
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

    public static String createTextBasedIngredientHtml(KciIngredient ingredient, String pathRoToot) {
        if (ingredient instanceof CustomItemIngredient) {
            KciItem item = ((CustomItemIngredient) ingredient).getItem();
            return "<a href=\"" + pathRoToot + "/items/" + item.getName() + ".html\">" + stripColorCodes(item.getDisplayName()) +
                    "</a> x " + ingredient.getAmount();
        } else if (ingredient instanceof SimpleVanillaIngredient) {
            return ((SimpleVanillaIngredient) ingredient).getMaterial() + " x " + ingredient.getAmount();
        } else if (ingredient instanceof DataVanillaIngredient) {
            DataVanillaIngredient dataIngredient = (DataVanillaIngredient) ingredient;
            return dataIngredient.getMaterial() + " with datavalue " + dataIngredient.getDataValue() + " x " + ingredient.getAmount();
        } else if (ingredient instanceof MimicIngredient) {
            return ((MimicIngredient) ingredient).getItemId() + " x " + ingredient.getAmount();
        } else if (ingredient instanceof ItemBridgeIngredient) {
            return ((ItemBridgeIngredient) ingredient).getItemId() + " x " + ingredient.getAmount();
        } else if (ingredient instanceof NoIngredient) {
            return "nothing";
        } else {
            throw new IllegalArgumentException("Unknown ingredient class: " + ingredient.getClass());
        }
    }

    public static String describePotionEffect(KciPotionEffect effect) {
        return NameHelper.getNiceEnumName(effect.getType().name()) + " " + effect.getLevel() + " for " + effect.getDuration() + " ticks";
    }

    public static String describePotionEffect(ChancePotionEffect effect) {
        return describePotionEffect(KciPotionEffect.createQuick(effect.getType(), effect.getDuration(), effect.getLevel()));
    }

    public static String getDisplayName(KciContainer container) {
        String niceDisplayName = stripColorCodes(container.getSelectionIcon().getDisplayName());
        if (niceDisplayName.isEmpty()) return container.getName();
        else return niceDisplayName;
    }

    public static void generateAudio(PrintWriter output, String tabs, String pathToRoot, KciSound sound) {
        if (sound.getCustomSound() != null) {
            output.println(tabs + "<audio controls>");
            output.println(tabs + "\t<source src=\"" + pathToRoot + "sounds/" + sound.getCustomSound().getName()
                    + ".ogg\" type=\"audio/ogg\">");
            output.println(tabs + "\tYour browser does not support (ogg) audio.");
            output.println(tabs + "</audio>");
        } else {
            output.println(tabs + "Plays the vanilla sound " + NameHelper.getNiceEnumName(sound.getVanillaSound().name()));
        }
    }
}
