package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.function.Consumer;

import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public abstract class EditProjectileEffect<V extends ProjectileEffectValues> extends GuiMenu {

	protected final V currentValues;
	private final Consumer<ProjectileEffectValues> applyChanges;
	private final GuiComponent returnMenu;

	protected final DynamicTextComponent errorComponent;

	@SuppressWarnings("unchecked")
	public EditProjectileEffect(V oldValues, Consumer<ProjectileEffectValues> applyChanges, GuiComponent returnMenu) {
		this.currentValues = (V) oldValues.copy(true);
		this.applyChanges = applyChanges;
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

		addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
			applyChanges.accept(currentValues);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.2f, 0.175f, 0.3f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
}
