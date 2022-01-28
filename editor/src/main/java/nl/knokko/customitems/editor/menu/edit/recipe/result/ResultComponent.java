/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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