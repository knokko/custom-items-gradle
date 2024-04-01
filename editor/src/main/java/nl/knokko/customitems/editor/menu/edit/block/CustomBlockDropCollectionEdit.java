package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.util.Chance;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class CustomBlockDropCollectionEdit extends SelfDedicatedCollectionEdit<CustomBlockDropValues> {

    private final ItemSet set;

    public CustomBlockDropCollectionEdit(
            Collection<CustomBlockDropValues> oldCollection,
            Consumer<List<CustomBlockDropValues>> changeCollection,
            ItemSet set, GuiComponent returnMenu
    ) {
        super(oldCollection, changeCollection, returnMenu);
        this.set = set;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add drop", EditProps.BUTTON, EditProps.HOVER, () ->
                state.getWindow().setMainComponent(new EditCustomBlockDrop(
                        new CustomBlockDropValues(false), set, this, this::addModel
                ))
        ), 0.025f, 0.3f, 0.2f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/drops/overview.html");
    }

    @Override
    public BufferedImage getModelIcon(CustomBlockDropValues drop) {

        /*
         * This operation is not so trivial because there can be any number of custom items to be dropped
         * and any number of required custom items (where 0 is also possible).
         *
         * I think it is the best to first try to find a custom dropped item. If only 1 is found, we
         * can just use its image. If multiple are found, we pick the one with the biggest chance to
         * be dropped. If multiple share the biggest drop chance, the choice is arbitrary.
         *
         * If no custom item can be dropped, we check if any custom item is required as tool to break
         * it. If we find 1, we use its image. If multiple are found, we pick the image of an arbitrary
         * candidate.
         *
         * If there are no required custom held items either, we give up and return null (don't show
         * an image).
         */
        int rawBestDropChance = 0;
        CustomItemValues bestDropItem = null;
        for (OutputTableValues.Entry dropEntry : drop.getDrop().getOutputTable().getEntries()) {
            ResultValues droppedItem = dropEntry.getResult();
            if (dropEntry.getChance().getRawValue() > rawBestDropChance && droppedItem instanceof CustomItemResultValues) {
                CustomItemResultValues droppedCustomItem = (CustomItemResultValues) droppedItem;
                bestDropItem = droppedCustomItem.getItem();
                rawBestDropChance = dropEntry.getChance().getRawValue();
            }
        }
        if (bestDropItem != null) {
            return bestDropItem.getTexture().getImage();
        }

        for (ItemReference requiredItem : drop.getDrop().getRequiredHeldItems().getCustomItems()) {
            return requiredItem.get().getTexture().getImage();
        }

        return null;
    }

    @Override
    public String getModelLabel(CustomBlockDropValues drop) {
        StringBuilder result = new StringBuilder();
        if (drop.getSilkTouchRequirement() == SilkTouchRequirement.REQUIRED) {
            result.append("[Silk] ");
        } else if (drop.getSilkTouchRequirement() == SilkTouchRequirement.FORBIDDEN) {
            result.append("[No silk] ");
        }

        Object likelyResult = null;
        int rawBestChance = 0;
        for (OutputTableValues.Entry entry : drop.getDrop().getOutputTable().getEntries()) {
            if (entry.getChance().getRawValue() > rawBestChance) {
                likelyResult = entry.getResult();
                rawBestChance = entry.getChance().getRawValue();
            }
        }
        if (likelyResult != null) {
            result.append(new Chance(rawBestChance)).append(" ").append(likelyResult);
        }
        if (drop.getDrop().getOutputTable().getEntries().size() > 1) {
            result.append(", ...");
        }

        return result.toString();
    }

    @Override
    public boolean canEditModel(CustomBlockDropValues model) {
        return true;
    }

    @Override
    public GuiComponent createEditMenu(CustomBlockDropValues dropToEdit, Consumer<CustomBlockDropValues> applyChanges) {
        return new EditCustomBlockDrop(dropToEdit, set, this, applyChanges);
    }

    @Override
    public CopyMode getCopyMode(CustomBlockDropValues drop) {
        return CopyMode.INSTANT;
    }

    @Override
    public boolean canDeleteModels() {
        return true;
    }
}
