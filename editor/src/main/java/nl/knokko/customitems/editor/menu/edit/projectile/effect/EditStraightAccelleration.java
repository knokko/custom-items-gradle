package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.Collection;

import nl.knokko.customitems.projectile.effects.ProjectileAccelleration;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.StraightAccelleration;
import nl.knokko.gui.component.GuiComponent;

public class EditStraightAccelleration extends EditAccelleration {

	public EditStraightAccelleration(ProjectileAccelleration oldValues, ProjectileAccelleration toModify, 
			Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu) {
		super(oldValues, toModify, backingCollection, returnMenu);
	}

	@Override
	protected ProjectileAccelleration create(float minAccelleration, float maxAccelleration) {
		return new StraightAccelleration(minAccelleration, maxAccelleration);
	}

	@Override
	protected String getURLEnd() {
		return "edit%20menu/projectiles/effects/edit/straight%20accellerate.html";
	}
}
