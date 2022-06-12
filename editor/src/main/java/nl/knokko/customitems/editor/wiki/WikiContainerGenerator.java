package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomPocketContainerValues;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.*;
import static nl.knokko.customitems.editor.wiki.WikiRecipeGenerator.generateContainerRecipe;

class WikiContainerGenerator {

    private final ItemSet itemSet;
    private final CustomContainerValues container;

    WikiContainerGenerator(ItemSet itemSet, CustomContainerValues container) {
        this.itemSet = itemSet;
        this.container = container;
    }

    void generate(File destination) throws IOException {
        generateHtml(destination, "../containers.css", getDisplayName(container), output -> {
            output.println("\t\t<h1>" + getDisplayName(container) + "</h1>");
            output.println("\t\t<h2>Basic information</h2>");
            output.println("\t\tInternal name: " + container.getName() + "<br>");

            long numFuelSlots = CustomContainerValues.createSlotList(container.getSlots()).stream().filter(
                    slot -> slot instanceof FuelSlotValues
            ).count();
            if (numFuelSlots == 1) {
                output.println("\t\tThis container has 1 fuel slot. Recipes can only progress while this slot is burning.<br>");
            }
            if (numFuelSlots > 1) {
                output.print("\t\tThis container has " + numFuelSlots + " fuel slots.");
                if (container.getFuelMode() == FuelMode.ALL) {
                    output.println("Recipes can only progress while <b>all</b> these fuel slots are burning.<br>");
                } else {
                    output.println("Recipes can only progress while <b>at least 1</b> of these fuel slots is burning.<br>");
                }
            }

            if (container.hasPersistentStorage()) {
                output.println("\t\tPlayers can store items in this container.<br>");
            } else {
                output.println("\t\tAll items that are stored in this container will be dropped on the floor when the container is closed.<br>");
            }

            output.println("\t\t<h2>Opening this container</h2>");

            if (container.getHost().getVanillaType() != null && container.getHost().getVanillaType() != VanillaContainerType.NONE) {
                output.println("\t\tLeft-click (hit) a " + NameHelper.getNiceEnumName(container.getHost().getVanillaType().name()) +
                        " while sneaking in survival mode<br>");
            }
            if (container.getHost().getVanillaMaterial() != null) {
                output.println("\t\tRight-click a " + NameHelper.getNiceEnumName(container.getHost().getVanillaMaterial().name()) + "<br>");
            }
            if (container.getHost().getCustomBlockReference() != null) {
                String blockName = container.getHost().getCustomBlockReference().get().getName();
                output.println("\t\tRight-click a <a href=\"../blocks/" + blockName + ".html\">" + blockName + "</a><br>");
            }

            Collection<CustomItemValues> pocketContainers = itemSet.getItems().stream().filter(
                    item -> item instanceof CustomPocketContainerValues && ((CustomPocketContainerValues) item).getContainers().stream().anyMatch(
                            candidateContainer -> candidateContainer.getName().equals(container.getName())
                    )
            ).collect(Collectors.toList());
            if (!pocketContainers.isEmpty()) {
                output.println("\t\tRight-click while holding 1 of these pocket containers in your hand:");
                output.println("\t\t<ul>");
                for (CustomItemValues pocketContainer : pocketContainers) {
                    output.println("\t\t\t<li><a href=\"../items/" + pocketContainer.getName() + ".html\">" +
                            stripColorCodes(pocketContainer.getDisplayName()) + "</a></li>");
                }
                output.println("\t\t</ul>");
            }

            output.println("\t\t<h2>Layout</h2>");
            generateLayout(output);

            if (!container.getRecipes().isEmpty()) {
                output.println("\t\t<h2>Recipes</h2>");
                output.println("\t\t<link rel=\"stylesheet\" href=\"../recipe.css\" />");
                for (ContainerRecipeValues recipe : container.getRecipes()) {
                    output.println("\t\tDuration: " + recipe.getDuration() + " ticks<br>");
                    output.println("\t\tExperience: " + recipe.getExperience() + "<br>");
                    generateContainerRecipe(output, "\t\t", container, recipe, "../");
                }
            }
        });
    }

    private void generateLayout(PrintWriter output) {
        output.println("\t\t<table class=\"layout-table\">");
        output.println("\t\t\t<tbody>");

        for (int row = 0; row < container.getHeight(); row++) {
            output.println("\t\t\t\t<tr>");

            for (int column = 0; column < container.getWidth(); column++) {
                generateLayoutSlot(output, container.getSlot(column, row));
            }

            output.println("\t\t\t\t</tr>");
        }

        output.println("\t\t\t</tbody>");
        output.println("\t\t</table>");
    }

    private void generateLayoutSlot(PrintWriter output, ContainerSlotValues slot) {
        output.print("\t\t\t\t\t<td class=\"layout-slot");
        if (slot instanceof DecorationSlotValues) {
            output.println(" layout-decoration-slot\">" + generateDisplay(((DecorationSlotValues) slot).getDisplay()) + "</td>");
        } else if (slot instanceof EmptySlotValues) {
            output.println(" layout-empty-slot\"></td>");
        } else if (slot instanceof FuelIndicatorSlotValues) {
            output.println(" layout-fuel-indicator-slot\">" + generateDisplay(((FuelIndicatorSlotValues) slot).getDisplay()) + "</td>");
        } else if (slot instanceof FuelSlotValues) {
            output.println(" layout-fuel-slot\">" + generateDisplay(((FuelSlotValues) slot).getPlaceholder()) + "</td>");
        } else if (slot instanceof InputSlotValues) {
            output.println(" layout-input-slot\">" + generateDisplay(((InputSlotValues) slot).getPlaceholder()) + "</td>");
        } else if (slot instanceof ManualOutputSlotValues) {
            output.println(" layout-manual-output-slot\">" + generateDisplay(((ManualOutputSlotValues) slot).getPlaceholder()) + "</td>");
        } else if (slot instanceof OutputSlotValues) {
            output.println(" layout-output-slot\">" + generateDisplay(((OutputSlotValues) slot).getPlaceholder()) + "</td>");
        } else if (slot instanceof ProgressIndicatorSlotValues) {
            output.println(" layout-progress-indicator-slot\">" + generateDisplay(((ProgressIndicatorSlotValues) slot).getDisplay()) + "</td>");
        } else if (slot instanceof StorageSlotValues) {
            output.println(" layout-storage-slot\">" + generateDisplay(((StorageSlotValues) slot).getPlaceholder()) + "</td>");
        } else {
            output.println("\">Unknown slot type</td>");
        }
    }

    private String generateDisplay(SlotDisplayValues display) {
        if (display == null) return "";
        SlotDisplayItemValues item = display.getDisplayItem();
        if (item instanceof CustomDisplayItemValues) {
            CustomItemValues custom = ((CustomDisplayItemValues) item).getItem();
            return "<a href=\"../items/" + custom.getName() + ".html\"><img src=\"../textures/" + custom.getTexture().getName() +
                    ".png\" class=\"layout-image\" /></a>";
        } else if (item instanceof SimpleVanillaDisplayItemValues) {
            return "Decoration: " + NameHelper.getNiceEnumName(((SimpleVanillaDisplayItemValues) item).getMaterial().name());
        } else if (item instanceof DataVanillaDisplayItemValues) {
            return "Decoration: " + NameHelper.getNiceEnumName(((DataVanillaDisplayItemValues) item).getMaterial().name());
        } else {
            return "Unknown decoration";
        }
    }
}
