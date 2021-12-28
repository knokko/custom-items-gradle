package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effect.ExecuteCommandValues;
import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditExecuteCommand extends EditProjectileEffect<ExecuteCommandValues> {
	
	private static final float BUTTON_X = 0.4f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	public EditExecuteCommand(
			ExecuteCommandValues oldValues, Consumer<ProjectileEffectValues> applyChanges, GuiComponent returnMenu
	) {
		super(oldValues, applyChanges, returnMenu);
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(
				new DynamicTextComponent("Command:", LABEL),
				LABEL_X - 0.15f, 0.7f, LABEL_X, 0.8f
		);
		addComponent(
				new EagerTextEditField(currentValues.getCommand(), EDIT_BASE, EDIT_ACTIVE, currentValues::setCommand),
				BUTTON_X, 0.71f, 0.99f, 0.79f
		);
		addComponent(
				new DynamicTextComponent("Executed by:", LABEL),
				LABEL_X - 0.18f, 0.6f, LABEL_X, 0.7f
		);
		addComponent(
				EnumSelect.createSelectButton(ExecuteCommandValues.Executor.class, currentValues::setExecutor, currentValues.getExecutor()),
				BUTTON_X, 0.61f, BUTTON_X + 0.2f, 0.69f
		);

		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/command.html");
	}
}
