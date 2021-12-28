package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.function.Consumer;

import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.customitems.projectile.effect.StraightAccelerationValues;
import nl.knokko.gui.component.GuiComponent;

public class EditStraightAccelleration extends EditAcceleration<StraightAccelerationValues> {

	public EditStraightAccelleration(
			StraightAccelerationValues oldValues, Consumer<ProjectileEffectValues> changeValues, GuiComponent returnMenu
	) {
		super(oldValues, changeValues, returnMenu);
	}

	@Override
	protected String getURLEnd() {
		return "edit%20menu/projectiles/effects/edit/straight%20accellerate.html";
	}
}
