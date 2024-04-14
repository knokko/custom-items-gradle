package nl.knokko.customitems.editor.menu.edit.worldgen;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.OreVeinGeneratorReference;
import nl.knokko.customitems.worldgen.BlockProducerValues;
import nl.knokko.customitems.worldgen.OreVeinGeneratorValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class OreVeinGeneratorCollectionEdit extends DedicatedCollectionEdit<OreVeinGeneratorValues, OreVeinGeneratorReference> {

    private final ItemSet itemSet;

    public OreVeinGeneratorCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(
                returnMenu, itemSet.oreGenerators.references(),
                generator -> Validation.toErrorString(() -> itemSet.oreGenerators.add(generator))
        );
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditOreVeinGenerator(
                    this, itemSet, new OreVeinGeneratorValues(true), null
            ));
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/worldgen/ore/overview.html");
    }

    @Override
    protected String getModelLabel(OreVeinGeneratorValues model) {
        return model.toString();
    }

    @Override
    protected BufferedImage getModelIcon(OreVeinGeneratorValues model) {
        List<BlockProducerValues.Entry> entries = model.getOreMaterial().getEntries();
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
    protected boolean canEditModel(OreVeinGeneratorValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(OreVeinGeneratorReference modelReference) {
        return new EditOreVeinGenerator(this, itemSet, modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(OreVeinGeneratorReference modelReference) {
        return Validation.toErrorString(() -> itemSet.oreGenerators.remove(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(OreVeinGeneratorReference modelReference) {
        return CopyMode.INSTANT;
    }

    @Override
    protected GuiComponent createCopyMenu(OreVeinGeneratorReference modelReference) {
        throw new UnsupportedOperationException("Copying should be INSTANT");
    }
}
