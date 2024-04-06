package nl.knokko.customitems.editor.menu.edit.recipe.result;

import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.recipe.ChooseCopiedItemStack;
import nl.knokko.customitems.recipe.result.CopiedResultValues;
import nl.knokko.gui.component.GuiComponent;

public class ChooseCopyResult extends ChooseCopiedItemStack {
	
	private final Consumer<CopiedResultValues> onChoose;

	public ChooseCopyResult(GuiComponent cancelMenu, Consumer<CopiedResultValues> onChoose) {
		super(cancelMenu);
		this.onChoose = onChoose;
	}


	@Override
	protected void onPaste(String content) {
		onChoose.accept(CopiedResultValues.createQuick(content));
	}
}
