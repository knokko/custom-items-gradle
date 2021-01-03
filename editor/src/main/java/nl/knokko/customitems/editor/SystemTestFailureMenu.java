package nl.knokko.customitems.editor;

import java.util.ArrayList;
import java.util.List;

import nl.knokko.customitems.editor.SystemTests.SystemTestResult;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class SystemTestFailureMenu extends GuiMenu {
	
	private final SystemTestResult result;
	private final int testCounter;
	
	public SystemTestFailureMenu(SystemTestResult result, int testCounter) {
		if (result == SystemTestResult.SUCCESS) {
			throw new IllegalArgumentException("The system tests succeeded, so don't visit this menu");
		}
		this.result = result;
		this.testCounter = testCounter;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextComponent(
				"System checks failed (attempt " + testCounter + "): " + result, EditProps.LABEL),
				0.1f, 0.9f, 0.9f, 1f
		);
		addComponent(new DynamicTextComponent(
				"It looks like your computer doesn't allow the Editor to perform "
				+ "critical tasks like writing and reading files.", EditProps.LABEL),
		0f, 0.8f, 1f, 0.9f);
		addComponent(new DynamicTextComponent(
				"This will probably cause errors when you need to save or export your"
				+ " items.", EditProps.LABEL),
		0f, 0.7f, 0.9f, 0.8f);
		addComponent(new DynamicTextComponent(
				"Possible causes (but there could be more):", EditProps.LABEL),
		0f, 0.6f, 0.5f, 0.7f);
		
		boolean weirdResult = result == SystemTestResult.INCORRECT_BINARY_FILE 
				|| result == SystemTestResult.INCORRECT_TEXT_FILE;
		boolean binaryResult = result == SystemTestResult.CANT_CREATE_BINARY_FILE
				|| result == SystemTestResult.CANT_READ_BINARY_FILE
				|| result == SystemTestResult.CANT_DELETE_BINARY_FILE;
		boolean textResult = result == SystemTestResult.CANT_CREATE_TEXT_FILE
				|| result == SystemTestResult.CANT_READ_TEXT_FILE
				|| result == SystemTestResult.CANT_DELETE_TEXT_FILE;
		
		boolean multiEditorResult = binaryResult || textResult 
				|| result == SystemTestResult.CANT_CREATE_DIRECTORIES;
		
		List<String> possibleCauses = new ArrayList<>();
		if (multiEditorResult) {
			possibleCauses.add("You opened 2 or more Editors at the same time");
		}
		if (weirdResult) {
			possibleCauses.add("This is very weird...");
		}
		if (binaryResult || result == SystemTestResult.CANT_CREATE_DIRECTORIES) {
			possibleCauses.add("The Editor doesn't have the privileges to manipulate files");
			possibleCauses.add("You have a very aggressive anti-virus");
		}
		if (textResult) {
			possibleCauses.add("You have an aggressive anti-virus (possibly Avast)");
		}
		
		for (int index = 0; index < possibleCauses.size(); index++) {
			addComponent(
					new DynamicTextComponent(possibleCauses.get(index), EditProps.LABEL),
					0.15f, 0.5f - 0.1f * index, 0.9f, 0.6f - 0.1f * index
			);
		}
		
		addComponent(new DynamicTextComponent(
				"If your anti-virus is the culprit, try disabling it temporarily now "
				+ "and right before saving/exporting", EditProps.LABEL
		), 0f, 0.1f, 1f, 0.2f);
		
		addComponent(new DynamicTextButton("Quit", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().stopRunning();
		}), 0.1f, 0f, 0.2f, 0.1f);
		addComponent(new DynamicTextButton("Retry", EditProps.BUTTON, EditProps.HOVER, () -> {
			SystemTestResult newResult = SystemTests.performTests();
			if (newResult == SystemTestResult.SUCCESS) {
				state.getWindow().setMainComponent(MainMenu.INSTANCE);
			} else {
				state.getWindow().setMainComponent(new SystemTestFailureMenu(newResult, testCounter + 1));
			}
		}), 0.45f, 0f, 0.55f, 0.1f);
		addComponent(new DynamicTextButton("Continue anyway", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.65f, 0f, 0.85f, 0.1f);
	}

	@Override
	public GuiColor getBackgroundColor() {
		return SimpleGuiColor.RED;
	}
}
