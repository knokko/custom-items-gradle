package nl.knokko.customitems.editor.menu.edit.drops.mob;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.MobDropReference;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class MobDropCollectionEdit extends DedicatedCollectionEdit<MobDropValues, MobDropReference> {
	
	private final EditMenu menu;

	public MobDropCollectionEdit(EditMenu menu) {
		super(menu, menu.getSet().getMobDrops().references(), newDrop -> Validation.toErrorString(() -> menu.getSet().addMobDrop(newDrop)));
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("New mob drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditMobDrop(menu.getSet(), this, null, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit menu/drops/mobs.html");
	}

	@Override
	protected String getModelLabel(MobDropValues model) {
		if (model.getRequiredName() == null) {
			return model.getDrop() + " for " + model.getEntityType();
		} else {
			return model.getDrop() + " for " + model.getRequiredName();
		}
	}

	@Override
	protected BufferedImage getModelIcon(MobDropValues model) {

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
	protected boolean canEditModel(MobDropValues model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(MobDropReference modelReference) {
		return new EditMobDrop(menu.getSet(), this, modelReference.get(), modelReference);
	}

	@Override
	protected String deleteModel(MobDropReference modelReference) {
		return Validation.toErrorString(() -> menu.getSet().removeMobDrop(modelReference));
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
