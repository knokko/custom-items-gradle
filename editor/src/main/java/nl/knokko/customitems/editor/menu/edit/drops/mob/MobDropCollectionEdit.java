package nl.knokko.customitems.editor.menu.edit.drops.mob;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.drops.MobDrop;
import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.MobDropReference;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class MobDropCollectionEdit extends DedicatedCollectionEdit<MobDrop, MobDropReference> {

	private final ItemSet itemSet;

	public MobDropCollectionEdit(ItemSet itemSet, GuiComponent returnMenu) {
		super(returnMenu, itemSet.mobDrops.references(), newDrop -> Validation.toErrorString(() -> itemSet.mobDrops.add(newDrop)));
		this.itemSet = itemSet;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("New mob drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditMobDrop(itemSet, this, new MobDrop(true), null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit menu/drops/mobs.html");
	}

	@Override
	protected String getModelLabel(MobDrop model) {
		String fullLabel;
		if (model.getRequiredName() == null) fullLabel = model.getDrop() + " for " + model.getEntityType();
		else fullLabel = model.getDrop() + " for " + model.getRequiredName();
		return StringLength.fixLength(fullLabel, 50);
	}

	@Override
	protected BufferedImage getModelIcon(MobDrop model) {

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
	protected boolean canEditModel(MobDrop model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(MobDropReference modelReference) {
		return new EditMobDrop(itemSet, this, modelReference.get(), modelReference);
	}

	@Override
	protected String deleteModel(MobDropReference modelReference) {
		return Validation.toErrorString(() -> itemSet.mobDrops.remove(modelReference));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(MobDropReference modelReference) {
		return CopyMode.INSTANT;
	}

	@Override
	protected GuiComponent createCopyMenu(MobDropReference modelReference) {
		throw new UnsupportedOperationException("CopyMode is INSTANT");
	}
}
