package nl.knokko.customitems.editor.menu.edit.worldgen;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.SafeCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.worldgen.BlockProducer;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class EditBlockProducer extends SafeCollectionEdit<BlockProducer.Entry> {

    private final ItemSet itemSet;

    private final Consumer<BlockProducer> changeValues;

    private Chance previousNothingChance = null;
    private final DynamicTextComponent nothingChanceComponent;

    public EditBlockProducer(
            GuiComponent returnMenu, ItemSet itemSet,
            BlockProducer oldValues, Consumer<BlockProducer> changeValues
    ) {
        super(returnMenu, Mutability.createDeepCopy(oldValues.getEntries(), true));
        this.itemSet = itemSet;
        this.changeValues = changeValues;
        this.nothingChanceComponent = new DynamicTextComponent("", EditProps.LABEL);
    }

    @Override
    public void update() {
        super.update();

        Chance currentNothingChance = BlockProducer.createQuick(new ArrayList<>(currentCollection)).getNothingChance();
        if (!Objects.equals(currentNothingChance, previousNothingChance)) {
            if (currentNothingChance != null) {
                nothingChanceComponent.setText("Chance to get nothing: " + currentNothingChance);
            } else {
                nothingChanceComponent.setText("Error: total chance > 100%");
            }
            previousNothingChance = currentNothingChance;
        }
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add entry", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new CreateBlockProducerEntry(
                    this, itemSet, currentCollection::add
            ));
        }), 0.025f, 0.55f, 0.2f, 0.65f);

        addComponent(nothingChanceComponent, 0f, 0.4f, 0.25f, 0.5f);

        HelpButtons.addHelpLink(this, "edit menu/worldgen/block producer.html");
    }

    @Override
    protected String getItemLabel(BlockProducer.Entry item) {
        return item.getChance() + " " + item.getBlock();
    }

    @Override
    protected BufferedImage getItemIcon(BlockProducer.Entry item) {
        if (item.getBlock().isCustom()) return item.getBlock().getCustomBlock().get().getModel().getPrimaryTexture().get().getImage();
        else return null;
    }

    @Override
    protected EditMode getEditMode(BlockProducer.Entry item) {
        // Entries are so simple that there is no need to edit them
        return EditMode.DISABLED;
    }

    @Override
    protected GuiComponent createEditMenu(BlockProducer.Entry itemToEdit) {
        throw new UnsupportedOperationException("Editing is disabled");
    }

    @Override
    protected String deleteItem(BlockProducer.Entry itemToDelete) {
        // Deleting entries is always allowed
        return null;
    }

    @Override
    protected CopyMode getCopyMode(BlockProducer.Entry item) {
        // Copying entries doesn't really make sense
        return CopyMode.DISABLED;
    }

    @Override
    protected BlockProducer.Entry copy(BlockProducer.Entry item) {
        return item.copy(true);
    }

    @Override
    protected GuiComponent createCopyMenu(BlockProducer.Entry itemToCopy) {
        throw new UnsupportedOperationException("Copying is disabled");
    }

    @Override
    protected boolean isCreatingNew() {
        // Always showing 'Apply' should be fine
        return false;
    }

    @Override
    protected void onApply() {
        BlockProducer producer = BlockProducer.createQuick(new ArrayList<>(currentCollection));
        String error = Validation.toErrorString(() -> producer.validate(itemSet));

        if (error == null) {
            changeValues.accept(producer);
            state.getWindow().setMainComponent(returnMenu);
        } else {
            errorComponent.setText(error);
        }
    }
}
