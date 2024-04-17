package nl.knokko.customitems.editor.menu.edit.recipe.result;

import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.recipe.ChooseCopiedItemStack;
import nl.knokko.customitems.recipe.result.CopiedResult;
import nl.knokko.gui.component.GuiComponent;

public class ChooseCopyResult extends ChooseCopiedItemStack {
	
	private final Consumer<CopiedResult> onChoose;

	public ChooseCopyResult(GuiComponent cancelMenu, Consumer<CopiedResult> onChoose) {
		super(cancelMenu);
		this.onChoose = onChoose;
	}


	@Override
	protected void onPaste(String content) {
		onChoose.accept(CopiedResult.createQuick(content));
	}
}
