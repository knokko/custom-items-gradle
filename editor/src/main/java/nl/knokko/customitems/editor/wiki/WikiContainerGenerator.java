package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.ContainerStorageMode;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.energy.RecipeEnergyOperation;
import nl.knokko.customitems.container.energy.RecipeEnergyValues;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomPocketContainerValues;
import nl.knokko.customitems.item.WikiVisibility;
import nl.knokko.customitems.itemset.ItemSet;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.*;
import static nl.knokko.customitems.editor.wiki.WikiRecipeGenerator.generateContainerRecipe;
import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

class WikiContainerGenerator {

    private final ItemSet itemSet;
    private final CustomContainerValues container;

    WikiContainerGenerator(ItemSet itemSet, CustomContainerValues container) {
        this.itemSet = itemSet;
        this.container = container;
    }

    private void generateOverlayTexture(File destination) throws IOException {
        if (container.getOverlayTexture() != null) {
            File overlayFolder = new File(destination.getParent() + "/overlay");
            if (!overlayFolder.isDirectory() && !overlayFolder.mkdirs()) {
                throw new IOException("Failed to create container overlay textures folder");
            }
            int height = container.getOverlayTexture().getHeight();
            ImageIO.write(
                    container.getOverlayTexture().getSubimage(40, 3, 176, height - 3), "PNG",
                    new File(overlayFolder + "/" + container.getName() + ".png")
            );
        }
    }

    void generate(File destination) throws IOException {
        generateOverlayTexture(destination);
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

            if (container.getStorageMode() == ContainerStorageMode.NOT_PERSISTENT) {
                output.println("\t\tAll items that are stored in this container will be dropped on the floor " +
                        "when the container is closed. (Just like a crafting table)<br>");
            } else if (container.getStorageMode() == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
                output.println("\t\tPlayers can store items in this container, " +
                        "which will be hidden from other players.<br>");
            } else if (container.getStorageMode() == ContainerStorageMode.PER_LOCATION) {
                output.println("\t\tPlayers can store items in this container. (Just like a chest or a furnace.)<br>");
            } else if (container.getStorageMode() == ContainerStorageMode.PER_PLAYER) {
                output.println("\t\tPlayers can store items in this container. " +
                        "These items will be hidden from other players and are shared with other containers of this type. " +
                        "(Just like an enderchest.)<br>");
            } else if (container.getStorageMode() == ContainerStorageMode.GLOBAL) {
                output.println("\t\tPlayers can store items in this container. " +
                        "These items will be shared with other containers of this type.<br>");
            } else {
                output.println("\t\tUnknown storage mode: " + container.getStorageMode());
            }
            if(container.requiresPermission()){
                output.println("\t\t<h2>Permissions Required: </h2>");
                output.println("\t\t<b>customitems.container.openany</b> or <b>customitems.container.open." + container.getName() + "</b> is required to use this container.");
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
                    ) && item.getWikiVisibility() == WikiVisibility.VISIBLE
            ).collect(Collectors.toList());
            if (!pocketContainers.isEmpty()) {
                output.println("\t\tRight-click while holding 1 of these pocket containers in your hand:");
                output.println("\t\t<ul class=\"pocket-containers\">");
                for (CustomItemValues pocketContainer : pocketContainers) {
                    output.println("\t\t\t<li class=\"pocket-container\"><a href=\"../items/" + pocketContainer.getName()
                            + ".html\">" + stripColorCodes(pocketContainer.getDisplayName()) + "</a></li>");
                }
                output.println("\t\t</ul>");
            }

            output.println("\t\t<h2>Layout</h2>");
            generateLayout(output);

            List<ContainerRecipeValues> recipes = container.getRecipes().stream().filter(
                    recipe -> !WikiProtector.isRecipeSecret(recipe)
            ).collect(Collectors.toList());
            if (!recipes.isEmpty()) {
                output.println("\t\t<h2>Recipes</h2>");
                output.println("\t\t<link rel=\"stylesheet\" href=\"../recipe.css\" />");
                for (ContainerRecipeValues recipe : recipes) {
                    output.println("\t\tDuration: " + recipe.getDuration() + " ticks<br>");
                    output.println("\t\tExperience: " + recipe.getExperience() + "<br>");
                    for (RecipeEnergyValues energy : recipe.getEnergy()) {
                        String energyLink = "<a href=\"energy/" + energy.getEnergyType().getName()
                                + ".html\">" + energy.getEnergyType().getName() + "</a>";
                        if (energy.getOperation() == RecipeEnergyOperation.REQUIRE_AT_LEAST) {
                            output.println("\t\tThis container must have at least " + energy.getAmount() + " " + energyLink + "<br>");
                        } else if (energy.getOperation() == RecipeEnergyOperation.DECREASE) {
                            output.println("\t\tPerforming this recipe decreases the " + energyLink + " of this container by "
                                    + energy.getAmount() + "<br>");
                        } else if (energy.getOperation() == RecipeEnergyOperation.REQUIRE_AT_MOST) {
                            output.println("\t\tThis container can have at most " + energy.getAmount() + " " + energyLink + "<br>");
                        } else if (energy.getOperation() == RecipeEnergyOperation.INCREASE) {
                            output.println("\t\tPerforming this recipe increases the " + energyLink + " of this container by "
                                    + energy.getAmount() + "<br>");
                        } else {
                            output.println("\t\tUnknown interaction with energy type " + energyLink + "<br>");
                        }
                    }
                    if(recipe.getRequiredPermission()!=null){
                        output.println("\t\t<b>" +recipe.getRequiredPermission() + "</b> or <b>customitems.container.recipe.any</b> permission is required by player to craft this item.");
                    }
                    generateContainerRecipe(output, "\t\t", container, recipe, "../", itemSet);
                    output.println("\t\t<br>");
                }
            }
        });
    }

    private void generateLayout(PrintWriter output) {
        String tableStyle;
        if (container.getOverlayTexture() != null) tableStyle = "background-image: url(overlay/" + container.getName() + ".png);";
        else tableStyle = "background-color: rgb(150,150,150);";

        output.println("\t\t<table class=\"layout-table\" style=\"" + tableStyle + "\">");
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
            String result = "<img src=\"../textures/" + custom.getTexture().getName() +
                    ".png\" class=\"layout-image\" />";
            if (custom.getWikiVisibility() == WikiVisibility.VISIBLE) {
                result = "<a href=\"../items/" + custom.getName() + ".html\">" + result + "</a>";
            }
            return result;
        } else if (item instanceof SimpleVanillaDisplayItemValues) {
            return "Decoration: " + NameHelper.getNiceEnumName(((SimpleVanillaDisplayItemValues) item).getMaterial().name());
        } else if (item instanceof DataVanillaDisplayItemValues) {
            return "Decoration: " + NameHelper.getNiceEnumName(((DataVanillaDisplayItemValues) item).getMaterial().name());
        } else {
            return "Unknown decoration";
        }
    }
}
