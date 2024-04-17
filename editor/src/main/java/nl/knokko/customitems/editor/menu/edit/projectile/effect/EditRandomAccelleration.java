package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.function.Consumer;

import nl.knokko.customitems.projectile.effect.ProjectileEffect;
import nl.knokko.customitems.projectile.effect.PERandomAcceleration;
import nl.knokko.gui.component.GuiComponent;

public class EditRandomAccelleration extends EditAcceleration<PERandomAcceleration> {

	public EditRandomAccelleration(
			PERandomAcceleration oldValues, Consumer<ProjectileEffect> changeValues, GuiComponent returnMenu
	) {
		super(oldValues, changeValues, returnMenu);
	}

	@Override
	protected String getURLEnd() {
		return "edit%20menu/projectiles/effects/edit/random%20accellerate.html";
	}
}
