package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.List;

public class ResultView extends GuiMenu {
	
	private final ResultComponent component;
	private final ItemSet set;

	public ResultView(ResultComponent component, ItemSet set) {
		this.component = component;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(component.getMenu());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new DynamicTextButton("Change", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseResult(component.getMenu(), component::setResult, set));
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