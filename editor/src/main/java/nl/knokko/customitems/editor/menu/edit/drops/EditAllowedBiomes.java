package nl.knokko.customitems.editor.menu.edit.drops;

import nl.knokko.customitems.drops.AllowedBiomes;
import nl.knokko.customitems.drops.VBiome;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditAllowedBiomes extends GuiMenu {

    private final Consumer<AllowedBiomes> changeValues;
    private final GuiComponent returnMenu;

    private final AllowedBiomes currentValues;

    public EditAllowedBiomes(
            AllowedBiomes oldValues, Consumer<AllowedBiomes> changeValues, GuiComponent returnMenu
    ) {
        this.changeValues = changeValues;
        this.returnMenu = returnMenu;
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(currentValues::validate);
            if (error != null) {
                errorComponent.setText(error);
            } else {
                changeValues.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        addComponent(new BiomeWhitelist(), 0.3f, 0f, 0.6f, 0.9f);
        addComponent(new BiomeBlacklist(), 0.65f, 0f, 0.95f, 0.9f);

        HelpButtons.addHelpLink(this, "edit menu/drops/allowed biomes.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class BiomeList extends GuiMenu {

        Collection<VBiome> currentBiomes;
        final Consumer<Collection<VBiome>> updateBiomes;

        BiomeList(Collection<VBiome> currentBiomes, Consumer<Collection<VBiome>> updateBiomes) {
            this.currentBiomes = currentBiomes;
            this.updateBiomes = updateBiomes;
        }

        @Override
        protected void addComponents() {
            float maxY = 0.9f;
            for (VBiome biome : currentBiomes) {
                addComponent(new DynamicTextComponent(biome.toString(), LABEL), 0.1f, maxY - 0.1f, 0.7f, maxY);
                addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                    currentBiomes.remove(biome);
                    updateBiomes.accept(currentBiomes);
                    clearComponents();
                    addComponents();
                }), 0.75f, maxY - 0.1f, 0.95f, maxY);
                maxY -= 0.12f;
            }

            addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
                state.getWindow().setMainComponent(
                        new EnumSelect<>(VBiome.class, newBiome -> {
                            currentBiomes.add(newBiome);
                            updateBiomes.accept(currentBiomes);
                            clearComponents();
                            addComponents();
                        }, candidateBiome -> !currentBiomes.contains(candidateBiome), EditAllowedBiomes.this)
                );
            }), 0.1f, maxY - 0.1f, 0.2f, maxY);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }

    private class BiomeWhitelist extends BiomeList {

        BiomeWhitelist() {
            super(currentValues.getWhitelist(), currentValues::setWhitelist);
        }

        @Override
        protected void addComponents() {
            super.addComponents();
            if (currentBiomes.isEmpty()) {
                addComponent(new DynamicTextComponent("Allow all biomes", LABEL), 0.1f, 0.9f, 0.7f, 1f);
            } else {
                addComponent(new DynamicTextComponent("Allow only these biomes:", LABEL), 0.1f, 0.9f, 0.9f, 1f);
            }
        }
    }

    private class BiomeBlacklist extends BiomeList {

        BiomeBlacklist() {
            super(currentValues.getBlacklist(), currentValues::setBlacklist);
        }

        @Override
        protected void addComponents() {
            super.addComponents();
            addComponent(new DynamicTextComponent("Except:", LABEL), 0.1f, 0.9f, 0.3f, 1f);
        }
    }
}
