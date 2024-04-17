package nl.knokko.customitems.editor.menu.edit.worldgen;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.FixedPointEditField;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.worldgen.BlockProducer;
import nl.knokko.customitems.worldgen.ProducedBlock;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateBlockProducerEntry extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    private final BlockProducer.Entry currentValues;
    private final Consumer<BlockProducer.Entry> onSelect;

    public CreateBlockProducerEntry(GuiComponent returnMenu, ItemSet itemSet, Consumer<BlockProducer.Entry> onSelect) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.currentValues = new BlockProducer.Entry(true);
        this.onSelect = onSelect;
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
                onSelect.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.4f, 0.175f, 0.5f);

        addComponent(new DynamicTextComponent("Chance:", LABEL), 0.25f, 0.7f, 0.35f, 0.8f);
        addComponent(new FixedPointEditField(
                Chance.NUM_BACK_DIGITS, Chance.percentage(100).getRawValue(), 0, 100,
                newRawValue -> currentValues.setChance(new Chance(newRawValue))
        ), 0.36f, 0.7f, 0.45f, 0.8f);

        DynamicTextComponent selectedBlockLabel = new DynamicTextComponent("Block: None", LABEL);
        addComponent(selectedBlockLabel, 0.25f, 0.55f, 0.5f, 0.65f);

        addComponent(new DynamicTextButton("Vanilla...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EnumSelect<>(
                    VMaterial.class, block -> {
                        currentValues.setBlock(new ProducedBlock(block));
                        selectedBlockLabel.setText("Block: " + block);
                    }, candidateBlock -> true, this
            ));
        }), 0.55f, 0.55f, 0.65f, 0.65f);

        addComponent(new DynamicTextButton("Custom...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CollectionSelect<>(
                    itemSet.blocks.references(), block -> {
                        currentValues.setBlock(new ProducedBlock(block));
                        selectedBlockLabel.setText("Block: " + block.get().getName());
                    }, candidateBlock -> true, candidateBlock -> candidateBlock.get().getName(), this, false
            ));
        }), 0.7f, 0.55f, 0.8f, 0.65f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
