package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.UpgradeResult;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ResultComponent extends DynamicTextButton {
	
	KciResult current;
	private final Consumer<KciResult> changeResult;

	private final GuiComponent menu;

	public ResultComponent(
            KciResult original, Consumer<KciResult> changeResult, GuiComponent menu, ItemSet set,
            BiFunction<GuiComponent, UpgradeResult, GuiComponent> chooseUpgradeIngredient
	) {
		super(original.toString(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, null);
		clickAction = () -> {
			state.getWindow().setMainComponent(new ResultView(this, set, chooseUpgradeIngredient));
		};
		current = original.copy(true);
		this.changeResult = changeResult;
		this.menu = menu;
	}
	
	public void setResult(KciResult newResult) {
		current = newResult;
		setText(newResult.toString());
		changeResult.accept(newResult);
	}

	public GuiComponent getMenu() {
		return menu;
	}
}