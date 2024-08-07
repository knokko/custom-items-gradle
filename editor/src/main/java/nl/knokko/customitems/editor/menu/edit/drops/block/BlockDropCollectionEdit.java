package nl.knokko.customitems.editor.menu.edit.drops.block;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.BlockDropReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class BlockDropCollectionEdit extends DedicatedCollectionEdit<BlockDrop, BlockDropReference> {

	private final ItemSet itemSet;

	public BlockDropCollectionEdit(ItemSet itemSet, GuiComponent returnMenu) {
		super(returnMenu, itemSet.blockDrops.references(), toAdd -> Validation.toErrorString(() -> itemSet.blockDrops.add(toAdd)));
		this.itemSet = itemSet;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("New block drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditBlockDrop(itemSet, this, new BlockDrop(true), null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit menu/drops/blocks.html");
	}

	@Override
	protected String getModelLabel(BlockDrop model) {
		String fullLabel = model.getDrop().toString() + " for " + model.getBlockType();
		return StringLength.fixLength(fullLabel, 50);
	}

	@Override
	protected BufferedImage getModelIcon(BlockDrop model) {

		// If we have any custom item drop, use that as icon!
		OutputTable dropTable = model.getDrop().getOutputTable();
		for (OutputTable.Entry entry : dropTable.getEntries()) {
			if (entry.getResult() instanceof CustomItemResult) {
				CustomItemResult customResult = (CustomItemResult) entry.getResult();
				return customResult.getItem().getTexture().getImage();
			}
		}

		// If we can't find one... well... that's unfortunate
		return null;
	}

	@Override
	protected boolean canEditModel(BlockDrop model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(BlockDropReference modelReference) {
		return new EditBlockDrop(itemSet, this, modelReference.get(), modelReference);
	}

	@Override
	protected String deleteModel(BlockDropReference modelReference) {
		return Validation.toErrorString(() -> itemSet.blockDrops.remove(modelReference));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(BlockDropReference modelReference) {
		return CopyMode.INSTANT;
	}

	@Override
	protected GuiComponent createCopyMenu(BlockDropReference modelReference) {
		throw new UnsupportedOperationException("CopyMode is INSTANT");
	}
}
