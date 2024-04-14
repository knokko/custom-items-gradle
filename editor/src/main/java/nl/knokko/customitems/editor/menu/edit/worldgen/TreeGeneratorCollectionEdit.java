package nl.knokko.customitems.editor.menu.edit.worldgen;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TreeGeneratorReference;
import nl.knokko.customitems.worldgen.BlockProducerValues;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class TreeGeneratorCollectionEdit extends DedicatedCollectionEdit<TreeGeneratorValues, TreeGeneratorReference> {

    private final ItemSet itemSet;

    public TreeGeneratorCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(
                returnMenu, itemSet.treeGenerators.references(),
                newGenerator -> Validation.toErrorString(() -> itemSet.treeGenerators.add(newGenerator))
        );
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditTreeGenerator(
                    this, itemSet, new TreeGeneratorValues(true), null
            ));
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/worldgen/tree/overview.html");
    }

    @Override
    protected String getModelLabel(TreeGeneratorValues model) {
        return model.toString();
    }

    @Override
    protected BufferedImage getModelIcon(TreeGeneratorValues model) {
        List<BlockProducerValues.Entry> entries = model.getLogMaterial().getEntries();
        entries.sort(Comparator.comparingInt(entry -> entry.getChance().getRawValue()));

        BufferedImage icon = null;
        for (BlockProducerValues.Entry entry : entries) {
            if (entry.getBlock().isCustom()) {
                icon = entry.getBlock().getCustomBlock().get().getModel().getPrimaryTexture().get().getImage();
            }
        }

        return icon;
    }

    @Override
    protected boolean canEditModel(TreeGeneratorValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(TreeGeneratorReference modelReference) {
        return new EditTreeGenerator(this, itemSet, modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(TreeGeneratorReference modelReference) {
        return Validation.toErrorString(() -> itemSet.treeGenerators.remove(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(TreeGeneratorReference modelReference) {
        return CopyMode.INSTANT;
    }

    @Override
    protected GuiComponent createCopyMenu(TreeGeneratorReference modelReference) {
        throw new UnsupportedOperationException("CopyMode is INSTANT");
    }
}
