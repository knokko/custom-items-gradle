package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.function.Consumer;

public class ResultComponent extends DynamicTextButton {
	
	ResultValues current;
	private final Consumer<ResultValues> changeResult;
	
	private final GuiComponent menu;

	public ResultComponent(ResultValues original, Consumer<ResultValues> changeResult, GuiComponent menu, ItemSet set) {
		super(original.toString(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, null);
		clickAction = () -> {
			state.getWindow().setMainComponent(new ResultView(this, set));
		};
		current = original.copy(true);
		this.changeResult = changeResult;
		this.menu = menu;
	}
	
	public void setResult(ResultValues newResult) {
		current = newResult;
		setText(newResult.toString());
		changeResult.accept(newResult);
	}

	public GuiComponent getMenu() {
		return menu;
	}
}