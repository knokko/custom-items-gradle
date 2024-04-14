package nl.knokko.customitems.editor.menu.edit.drops.block;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.BlockDropReference;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class BlockDropCollectionEdit extends DedicatedCollectionEdit<BlockDropValues, BlockDropReference> {
	
	private final EditMenu menu;

	public BlockDropCollectionEdit(EditMenu menu) {
		super(menu, menu.getSet().blockDrops.references(), toAdd -> Validation.toErrorString(() -> menu.getSet().blockDrops.add(toAdd)));
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("New block drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditBlockDrop(menu.getSet(), this, new BlockDropValues(true), null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit menu/drops/blocks.html");
	}

	@Override
	protected String getModelLabel(BlockDropValues model) {
		String fullLabel = model.getDrop().toString() + " for " + model.getBlockType();
		return StringLength.fixLength(fullLabel, 50);
	}

	@Override
	protected BufferedImage getModelIcon(BlockDropValues model) {

		// If we have any custom item drop, use that as icon!
		OutputTableValues dropTable = model.getDrop().getOutputTable();
		for (OutputTableValues.Entry entry : dropTable.getEntries()) {
			if (entry.getResult() instanceof CustomItemResultValues) {
				CustomItemResultValues customResult = (CustomItemResultValues) entry.getResult();
				return customResult.getItem().getTexture().getImage();
			}
		}

		// If we can't find one... well... that's unfortunate
		return null;
	}

	@Override
	protected boolean canEditModel(BlockDropValues model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(BlockDropReference modelReference) {
		return new EditBlockDrop(menu.getSet(), this, modelReference.get(), modelReference);
	}

	@Override
	protected String deleteModel(BlockDropReference modelReference) {
		return Validation.toErrorString(() -> menu.getSet().blockDrops.remove(modelReference));
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
