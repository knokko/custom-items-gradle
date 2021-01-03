package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class OutputSlotComponent implements GuiComponent {
	
	private static final GuiColor BASE_COLOR = new SimpleGuiColor(200, 0, 0);
	private static final GuiColor HOVER_COLOR = new SimpleGuiColor(200, 100, 100);
	
	private final String name;
	private final GuiComponent outerMenu;
	private final Collection<OutputEntry> outputs;
	private final ItemSet set;
	
	private GuiComponentState state;
	private GuiTexture topTextTexture;
	private GuiTexture bottomTextTexture;
	
	public OutputSlotComponent(String name, GuiComponent outerMenu, 
			Collection<OutputEntry> outputs, ItemSet set) {
		this.name = name;
		this.outerMenu = outerMenu;
		this.outputs = outputs;
		this.set = set;
	}
	
	public String getName() {
		return name;
	}
	
	private OutputEntry getOwnEntry() {
		for (OutputEntry entry : outputs) {
			if (entry.getOutputSlotName().equals(name)) {
				return entry;
			}
		}
		
		return null;
	}
	
	private OutputTable getOwnResultTable() {
		OutputEntry ownEntry = getOwnEntry();
		if (ownEntry != null) {
			return ownEntry.getOutputTable();
		} else {
			return null;
		}
	}
	
	private void setResultTable(OutputTable newResultTable) {
		
		// Update outputs collection
		OutputEntry ownEntry = getOwnEntry();
		if (ownEntry != null) {
			outputs.remove(ownEntry);
		}
		if (newResultTable != null) {
			outputs.add(new OutputEntry(name, newResultTable));
		}
		
		// Update text
		String bottomText = newResultTable == null ? "" : newResultTable.toString();
		
		int maxLength = 12;
		if (bottomText.length() > maxLength) {
			bottomText = bottomText.substring(0, maxLength);
		}
		
		this.bottomTextTexture = state.getWindow().getTextureLoader().loadTexture(
				TextBuilder.createTexture(bottomText, EditProps.LABEL)
		);
		state.getWindow().markChange();
	}

	@Override
	public void init() {
		this.topTextTexture = state.getWindow().getTextureLoader().loadTexture(
				TextBuilder.createTexture("output", EditProps.LABEL)
		);
		this.setResultTable(this.getOwnResultTable());
	}

	@Override
	public void setState(GuiComponentState state) {
		this.state = state;
	}

	@Override
	public GuiComponentState getState() {
		return state;
	}

	@Override
	public void update() {}

	@Override
	public void render(GuiRenderer renderer) {
		renderer.fill(state.isMouseOver() ? BASE_COLOR : HOVER_COLOR, 
				0.1f, 0.1f, 0.9f, 0.9f
		);
		renderer.renderTexture(topTextTexture, 0.1f, 0.6f, 0.9f, 0.9f);
		renderer.renderTexture(bottomTextTexture, 0.1f, 0.1f, 0.9f, 0.4f);
	}

	@Override
	public void click(float x, float y, int button) {
		state.getWindow().setMainComponent(new EditOutputTable(
				outerMenu, getOwnResultTable(), this::setResultTable, set
		));
	}

	@Override
	public void clickOut(int button) {}

	@Override
	public boolean scroll(float amount) {
		return false;
	}

	@Override
	public void keyPressed(int keyCode) {}

	@Override
	public void keyPressed(char character) {}

	@Override
	public void keyReleased(int keyCode) {}
}
