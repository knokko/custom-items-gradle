package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.UpgradeResult;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.List;
import java.util.function.BiFunction;

public class ResultView extends GuiMenu {
	
	private final ResultComponent component;
	private final ItemSet set;
	private final BiFunction<GuiComponent, UpgradeResult, GuiComponent> chooseUpgradeIngredient;

	public ResultView(
			ResultComponent component, ItemSet set,
			BiFunction<GuiComponent, UpgradeResult, GuiComponent> chooseUpgradeIngredient
	) {
		this.component = component;
		this.set = set;
		this.chooseUpgradeIngredient = chooseUpgradeIngredient;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(component.getMenu());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new DynamicTextButton("Change", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseResult(
					component.getMenu(), component::setResult, set,
					false, component.current, chooseUpgradeIngredient
			));
		}), 0.1f, 0.3f, 0.25f, 0.4f);

		List<String> info = component.current.getInfo();
		for (int index = 0; index < info.size(); index++) {
			addComponent(
					new DynamicTextComponent(info.get(index), EditProps.LABEL),
					0.4f, 0.8f - index * 0.15f, 0.7f, 0.9f - index * 0.15f
			);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}