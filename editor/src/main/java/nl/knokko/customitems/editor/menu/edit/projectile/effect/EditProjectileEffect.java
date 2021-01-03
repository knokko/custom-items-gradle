package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.Collection;

import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public abstract class EditProjectileEffect extends GuiMenu {
	
	protected final Collection<ProjectileEffect> backingCollection;
	protected final GuiComponent returnMenu;
	protected final DynamicTextComponent errorComponent;

	public EditProjectileEffect(Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu) {
		this.backingCollection = backingCollection;
		this.returnMenu = returnMenu;
		this.errorComponent = new DynamicTextComponent("", ERROR);
	}

	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}
	
	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.165f, 0.8f);
		
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
}
