package nl.knokko.customitems.editor.menu.edit.worldgen;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.worldgen.ReplaceBlocks;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Set;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditReplaceBlocks extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    private final ReplaceBlocks currentValues;
    private final Consumer<ReplaceBlocks> changeValues;

    public EditReplaceBlocks(
            GuiComponent returnMenu, ItemSet itemSet,
            ReplaceBlocks oldValues, Consumer<ReplaceBlocks> changeValues
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.currentValues = oldValues.copy(true);
        this.changeValues = changeValues;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(() -> currentValues.validate(itemSet));
            if (error == null) {
                changeValues.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.3f, 0.175f, 0.4f);

        addComponent(new DynamicTextComponent("Vanilla blocks:", LABEL), 0.225f, 0.8f, 0.4f, 0.9f);
        VanillaBlocks vanillaBlocks = new VanillaBlocks();
        addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
            state.getWindow().setMainComponent(new EnumSelect<>(
                    VMaterial.class, newBlock -> {
                        Set<VMaterial> newVanillaBlocks = currentValues.getVanillaBlocks();
                        newVanillaBlocks.add(newBlock);
                        currentValues.setVanillaBlocks(newVanillaBlocks);
                        vanillaBlocks.refresh();
                    }, candidateBlock -> !currentValues.contains(candidateBlock), this
            ));
        }), 0.425f, 0.8f, 0.5f, 0.9f);
        addComponent(vanillaBlocks, 0.225f, 0.05f, 0.575f, 0.8f);

        addComponent(new DynamicTextComponent("Custom blocks:", LABEL), 0.625f, 0.8f, 0.8f, 0.9f);
        CustomBlocks customBlocks = new CustomBlocks();
        addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
            state.getWindow().setMainComponent(new CollectionSelect<>(
                    itemSet.blocks.references(), newBlock -> {
                        Set<BlockReference> newBlocks = currentValues.getCustomBlocks();
                        newBlocks.add(newBlock);
                        currentValues.setCustomBlocks(newBlocks);
                        customBlocks.refresh();
                    }, candidateBlock -> !currentValues.contains(candidateBlock),
                    candidateBlock -> candidateBlock.get().getName(), this, false
            ));
        }), 0.825f, 0.8f, 0.9f, 0.9f);
        addComponent(customBlocks, 0.625f, 0.05f, 0.975f, 0.8f);

        HelpButtons.addHelpLink(this, "edit menu/worldgen/ore/replace blocks.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class VanillaBlocks extends GuiMenu {

        @Override
        protected void addComponents() {
            int index = 0;
            for (VMaterial vanillaBlock : currentValues.getVanillaBlocks()) {
                float maxY = 1f - 0.125f * index;
                float minY = maxY - 0.1f;

                addComponent(new DynamicTextComponent(vanillaBlock.toString(), LABEL), 0.05f, minY, 0.7f, maxY);
                addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                    Set<VMaterial> newVanillaBlocks = currentValues.getVanillaBlocks();
                    newVanillaBlocks.remove(vanillaBlock);
                    currentValues.setVanillaBlocks(newVanillaBlocks);
                    refresh();
                }), 0.75f, minY, 0.95f, maxY);

                index++;
            }
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }

        private void refresh() {
            clearComponents();
            addComponents();
        }
    }

    private class CustomBlocks extends GuiMenu {

        @Override
        protected void addComponents() {
            int index = 0;
            for (BlockReference customBlock : currentValues.getCustomBlocks()) {
                float maxY = 1f - 0.125f * index;
                float minY = maxY - 0.1f;

                addComponent(new DynamicTextComponent(customBlock.get().getName(), LABEL), 0.05f, minY, 0.7f, maxY);
                addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                    Set<BlockReference> newCustomBlocks = currentValues.getCustomBlocks();
                    newCustomBlocks.remove(customBlock);
                    currentValues.setCustomBlocks(newCustomBlocks);
                    refresh();
                }), 0.75f, minY, 0.95f, maxY);

                index++;
            }
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }

        private void refresh() {
            clearComponents();
            addComponents();
        }
    }
}
