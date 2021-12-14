package nl.knokko.customitems.editor.menu.edit.recipe.result;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.recipe.result.CopiedResultValues;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.keycode.KeyCode;

public class ChooseCopyResult extends GuiMenu {
	
	private final GuiComponent cancelMenu;
	private final Consumer<CopiedResultValues> onChoose;
	
	private final DynamicTextComponent errorComponent;
	
	public ChooseCopyResult(GuiComponent cancelMenu, Consumer<CopiedResultValues> onChoose) {
		this.cancelMenu = cancelMenu;
		this.onChoose = onChoose;
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(cancelMenu);
		}), 0.4f, 0.1f, 0.6f, 0.2f);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		addComponent(new DynamicTextComponent(
				"1. Visit your server in minecraft", EditProps.LABEL), 
				0.1f, 0.8f, 0.5f, 0.9f
		);
		addComponent(new DynamicTextComponent(
				"2. Hold the desired item stack in your main hand", EditProps.LABEL),
				0.1f, 0.7f, 0.7f, 0.8f
		);
		addComponent(new DynamicTextComponent(
				"3. Use the command /kci encode", EditProps.LABEL),
				0.1f, 0.6f, 0.5f, 0.7f
		);
		addComponent(new DynamicTextComponent(
				"4. Open your server console and copy the code", EditProps.LABEL),
				0.1f, 0.5f, 0.7f, 0.6f
		);
		addComponent(new DynamicTextComponent(
				"5. Paste that code somewhere in this menu with Control V", EditProps.LABEL),
				0.1f, 0.4f, 0.8f, 0.5f
		);
		addComponent(new DynamicTextComponent(
				"The code should contain only the first 16 letters of the alphabet", EditProps.LABEL),
				0.1f, 0.25f, 0.9f, 0.35f
		);
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void keyPressed(int key) {
		if (state.getWindow().getInput().isKeyDown(KeyCode.KEY_CONTROL)) {
			if (key == KeyCode.KEY_V) {
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				if (clipboard == null) {
					errorComponent.setText("It looks like this computer doesn't have a clipboard");
					return;
				}
				
				try {
					Transferable clipboardContent = clipboard.getContents(this);
					String code = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
					if (code.isEmpty()) {
						errorComponent.setText("It looks like you don't have any text on your clipboard");
						return;
					}
					try {
						StringEncoder.decode(code);
						onChoose.accept(CopiedResultValues.createQuick(code));
					} catch (IllegalArgumentException badCode) {
						errorComponent.setText("It looks like you don't have a valid code on your clipboard");
						System.err.println(badCode.getMessage());
					}
				} catch (IllegalStateException ex) {
					errorComponent.setText("It looks like your clipboard was unavailable. Please try again");
				} catch (UnsupportedFlavorException e) {
					errorComponent.setText("It looks like your clipboard doesn't support text");
				} catch (IOException e) {
					errorComponent.setText("It looks like the copied code is no longer available. Please copy it again");
				}
			}
		}
	}
}
