package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.sound.EditSound;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.effect.*;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileEffectCollectionEdit extends SelfDedicatedCollectionEdit<ProjectileEffect> {
	
	private final ItemSet set;

	public ProjectileEffectCollectionEdit(
			ItemSet set, Collection<ProjectileEffect> oldCollection,
			Consumer<List<ProjectileEffect>> changeCollection, GuiComponent returnMenu
	) {
		super(oldCollection, changeCollection, returnMenu);
		this.set = set;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			state.getWindow().setMainComponent(new CreateProjectileEffect(this::addModel, set, this
			));
		}), 0.025f, 0.2f, 0.15f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/overview.html");
	}

	@Override
	public BufferedImage getModelIcon(ProjectileEffect item) {
													   return null;
																   }

	@Override
	protected boolean canEditModel(ProjectileEffect model) {
		return true;
	}

	@Override
	public String getModelLabel(ProjectileEffect item) {
												return item.toString();
																	   }

	@Override
	public GuiComponent createEditMenu(ProjectileEffect oldValues, Consumer<ProjectileEffect> changeValues) {
		if (oldValues instanceof PEColoredRedstone) {
			return new EditColoredRedstone((PEColoredRedstone) oldValues, changeValues, this);
		} else if (oldValues instanceof PEExecuteCommand) {
			return new EditExecuteCommand((PEExecuteCommand) oldValues, changeValues, this);
		} else if (oldValues instanceof PECreateExplosion) {
			return new EditExplosion((PECreateExplosion) oldValues, changeValues, this);
		} else if (oldValues instanceof PERandomAcceleration) {
			return new EditRandomAccelleration((PERandomAcceleration) oldValues, changeValues, this);
		} else if (oldValues instanceof PESimpleParticle) {
			return new EditSimpleParticles((PESimpleParticle) oldValues, changeValues, this);
		} else if (oldValues instanceof PEStraightAcceleration) {
			return new EditStraightAccelleration((PEStraightAcceleration) oldValues, changeValues, this);
		} else if (oldValues instanceof PESubProjectiles) {
			return new EditSubProjectiles((PESubProjectiles) oldValues, changeValues, this, set);
		} else if (oldValues instanceof PEPlaySound) {
			return new EditSound(((PEPlaySound) oldValues).getSound(), newSound -> {
				changeValues.accept(PEPlaySound.createQuick(newSound));
			}, this, set);
		} else if (oldValues instanceof PEPotionAura) {
			return new EditPotionAura((PEPotionAura) oldValues, changeValues, this);
		} else if (oldValues instanceof PEPushOrPull) {
			return new EditPushOrPull((PEPushOrPull) oldValues, changeValues, this);
		} else if (oldValues instanceof PEShowFireworks) {
			return new EditShowFirework((PEShowFireworks) oldValues, changeValues, this);
		} else {
			throw new Error("Unknown projectile effect class: " + oldValues.getClass());
		}
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ProjectileEffect model) {
		return CopyMode.INSTANT;
	}
}
