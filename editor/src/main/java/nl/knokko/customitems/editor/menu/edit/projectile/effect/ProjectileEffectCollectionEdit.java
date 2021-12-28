package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.projectile.effect.*;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileEffectCollectionEdit extends SelfDedicatedCollectionEdit<ProjectileEffectValues> {
	
	private final SItemSet set;

	public ProjectileEffectCollectionEdit(
			SItemSet set, Collection<ProjectileEffectValues> oldCollection,
			Consumer<List<ProjectileEffectValues>> changeCollection, GuiComponent returnMenu
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
	public BufferedImage getModelIcon(ProjectileEffectValues item) {
													   return null;
																   }

	@Override
	protected boolean canEditModel(ProjectileEffectValues model) {
		return true;
	}

	@Override
	public String getModelLabel(ProjectileEffectValues item) {
												return item.toString();
																	   }

	@Override
	public GuiComponent createEditMenu(ProjectileEffectValues oldValues, Consumer<ProjectileEffectValues> changeValues) {
		if (oldValues instanceof ColoredRedstoneValues) {
			return new EditColoredRedstone((ColoredRedstoneValues) oldValues, changeValues, this);
		} else if (oldValues instanceof ExecuteCommandValues) {
			return new EditExecuteCommand((ExecuteCommandValues) oldValues, changeValues, this);
		} else if (oldValues instanceof ExplosionValues) {
			return new EditExplosion((ExplosionValues) oldValues, changeValues, this);
		} else if (oldValues instanceof RandomAccelerationValues) {
			return new EditRandomAccelleration((RandomAccelerationValues) oldValues, changeValues, this);
		} else if (oldValues instanceof SimpleParticleValues) {
			return new EditSimpleParticles((SimpleParticleValues) oldValues, changeValues, this);
		} else if (oldValues instanceof StraightAccelerationValues) {
			return new EditStraightAccelleration((StraightAccelerationValues) oldValues, changeValues, this);
		} else if (oldValues instanceof SubProjectilesValues) {
			return new EditSubProjectiles((SubProjectilesValues) oldValues, changeValues, this, set);
		} else if (oldValues instanceof PlaySoundValues) {
			return new EditPlaySound((PlaySoundValues) oldValues, changeValues, this);
		} else if (oldValues instanceof PotionAuraValues) {
			return new EditPotionAura((PotionAuraValues) oldValues, changeValues, this);
		} else if (oldValues instanceof PushOrPullValues) {
			return new EditPushOrPull((PushOrPullValues) oldValues, changeValues, this);
		} else if (oldValues instanceof ShowFireworkValues) {
			return new EditShowFirework((ShowFireworkValues) oldValues, changeValues, this);
		} else {
			throw new Error("Unknown projectile effect class: " + oldValues.getClass());
		}
	}

	@Override
	protected boolean canDeleteModels() {
		return false;
	}

	@Override
	protected CopyMode getCopyMode(ProjectileEffectValues model) {
		return CopyMode.INSTANT;
	}
}
