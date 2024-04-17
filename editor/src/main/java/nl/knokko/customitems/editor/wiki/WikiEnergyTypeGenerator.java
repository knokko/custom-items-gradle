package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.energy.EnergyType;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.File;
import java.io.IOException;

import static nl.knokko.customitems.editor.wiki.WikiHelper.generateHtml;
import static nl.knokko.customitems.editor.wiki.WikiHelper.getDisplayName;

class WikiEnergyTypeGenerator {

    private final ItemSet itemSet;
    private final EnergyType energyType;

    WikiEnergyTypeGenerator(ItemSet itemSet, EnergyType energyType) {
        this.itemSet = itemSet;
        this.energyType = energyType;
    }

    void generate(File destination) throws IOException {
        generateHtml(destination, "../energy.css", energyType.getName(), output -> {
            output.println("\t\t<h1>" + energyType.getName() + "</h1>");
            output.println("\t\t<h2 id=\"basic-properties-header\">Basic properties</h2>");
            output.println("\t\tMinimum value: " + energyType.getMinValue() + "<br>");
            output.println("\t\tMaximum value: " + energyType.getMaxValue() + "<br>");
            output.println("\t\tInitial value: " + energyType.getInitialValue() + "<br>");
            output.println("\t\t<h2 id=\"sharing-properties-header\">Sharing properties</h2>");
            if (energyType.shouldForceShareWithOtherContainerTypes()) {
                output.println("\t\tThis energy type will be shared between different containers at the same location.<br>");
            } else {
                output.println("\t\tEach container type will have its own storage of this energy type.<br>");
            }
            if (energyType.shouldForceShareWithOtherLocations()) {
                output.println("\t\tContainers at different locations will share their storage of this energy type, " +
                        "even if they don't share their inventory.<br>");
            }
            if (energyType.shouldForceShareWithOtherPlayers()) {
                output.println("\t\tPlayers will share their storage of this energy type, " +
                        "even if they don't share their container inventory.");
            }
            if (itemSet.containers.stream().anyMatch(this::usesThisEnergy)) {
                output.println("\t\t<h2 id=\"containers-header\">Containers</h2>");
                output.println("\t\t<ul>");
                for (KciContainer container : itemSet.containers) {
                    if (usesThisEnergy(container)) {
                        output.println("\t\t\t<li><a href=\"../" + container.getName() + ".html\">" + getDisplayName(container) + "</a></li>");
                    }
                }
                output.println("\t\t</ul>");
            }
        });
    }

    private boolean usesThisEnergy(KciContainer container) {
        return container.getRecipes().stream().anyMatch(
            recipe -> recipe.getEnergy().stream().anyMatch(
                    energy -> energy.getEnergyType().getId().equals(energyType.getId())
            )
        );
    }
}
