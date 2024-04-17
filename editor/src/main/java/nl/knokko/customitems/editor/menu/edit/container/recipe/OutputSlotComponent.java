package nl.knokko.customitems.editor.menu.edit.container.recipe;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.mousecode.MouseCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class OutputSlotComponent implements GuiComponent {
	
	private static final GuiColor BASE_COLOR = new SimpleGuiColor(200, 0, 0);
	private static final GuiColor HOVER_COLOR = new SimpleGuiColor(200, 100, 100);
	
	private final String name;
	private final GuiComponent outerMenu;
	private final ItemSet set;
	private final KciContainer container;
	private final ContainerRecipe recipe;
	private final OutputTable[] pClipboardResult;
	
	private GuiComponentState state;
	private GuiTexture topTextTexture;
	private GuiTexture bottomTextTexture;
	
	public OutputSlotComponent(
			String name, GuiComponent outerMenu, OutputTable[] pClipboardResult,
			KciContainer container, ContainerRecipe recipe, ItemSet set
	) {
		this.name = name;
		this.outerMenu = outerMenu;
		this.container = container;
		this.recipe = recipe;
		this.set = set;
		this.pClipboardResult = pClipboardResult;
	}
	
	public String getName() {
		return name;
	}

	private void setResultTable(OutputTable newResultTable) {
		
		// Update outputs collection
		OutputTable ownEntry = recipe.getOutput(name);
		if (ownEntry != null) {
			recipe.clearOutput(name);
		}
		if (newResultTable != null) {
			recipe.setOutput(name, newResultTable);
		}
		
		// Update text
		String bottomText = newResultTable == null ? "" : StringLength.fixLength(newResultTable.toString(), 12);
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
		this.setResultTable(recipe.getOutput(name));
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
		if (button == MouseCode.BUTTON_LEFT) {
			OutputTable ownTable = recipe.getOutput(name);
			state.getWindow().setMainComponent(new EditOutputTable(
					outerMenu, ownTable == null ? new OutputTable(true) : ownTable, this::setResultTable, set,
					(returnMenu, upgrade) -> new ChooseContainerIngredientForUpgrade(returnMenu, upgrade, container, recipe)
			));
		} else if (button == MouseCode.BUTTON_RIGHT) {
			this.setResultTable(null);
		}
	}

	@Override
	public void clickOut(int button) {}

	@Override
	public boolean scroll(float amount) {
		return false;
	}

	@Override
	public void keyPressed(int keyCode) {
		if (keyCode == KeyCode.KEY_P && state.isMouseOver()) {
			this.setResultTable(pClipboardResult[0]);
		}
		if (keyCode == KeyCode.KEY_C && state.isMouseOver()) {
			pClipboardResult[0] = recipe.getOutput(this.name);
		}
	}

	@Override
	public void keyPressed(char character) {}

	@Override
	public void keyReleased(int keyCode) {}
}
