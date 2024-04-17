package nl.knokko.customitems.editor.menu.edit.container.recipe;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.EditIngredient;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class InputSlotComponent implements GuiComponent {
	
	private static final GuiColor BASE_COLOR = new SimpleGuiColor(0, 0, 200);
	private static final GuiColor HOVER_COLOR = new SimpleGuiColor(100, 100, 200);
	
	private final String name;
	private final GuiComponent outerMenu;
	private final ItemSet set;
	private final ContainerRecipe recipe;
	private final KciIngredient[] pClipboardIngredient;
	
	private GuiComponentState state;
	private GuiTexture topTextTexture;
	private GuiTexture bottomTextTexture;

	public InputSlotComponent(
            String name, GuiComponent outerMenu, KciIngredient[] pClipboardIngredient,
            ContainerRecipe recipe, ItemSet set
	) {
		this.name = name;
		this.outerMenu = outerMenu;
		this.pClipboardIngredient = pClipboardIngredient;
		this.recipe = recipe;
		this.set = set;
	}
	
	public String getName() {
		return name;
	}
	
	private void setIngredient(KciIngredient newIngredient) {
		
		// Make sure only null indicates that there is no ingredient
		if (newIngredient instanceof NoIngredient) {
			newIngredient = null;
		}
		
		// Update inputs collection
		KciIngredient currentIngredient = recipe.getInput(name);
		if (currentIngredient != null) {
			recipe.clearInput(name);
		}
		if (newIngredient != null) {
			recipe.setInput(name, newIngredient);
		}
		
		// Update text
		String bottomText = newIngredient == null ? "" : StringLength.fixLength(newIngredient.toString(""), 18);
		this.bottomTextTexture = state.getWindow().getTextureLoader().loadTexture(
				TextBuilder.createTexture(bottomText, EditProps.LABEL)
		);
		state.getWindow().markChange();
	}

	@Override
	public void init() {
		this.topTextTexture = state.getWindow().getTextureLoader().loadTexture(
				TextBuilder.createTexture("input", EditProps.LABEL)
		);
		this.setIngredient(recipe.getInput(name));
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
		KciIngredient currentInput = recipe.getInput(name);
		if (currentInput == null) currentInput = new SimpleVanillaIngredient(false);
		state.getWindow().setMainComponent(new EditIngredient(outerMenu,
				this::setIngredient, currentInput, true, set
		));
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
			this.setIngredient(pClipboardIngredient[0]);
		}
		if (keyCode == KeyCode.KEY_C && state.isMouseOver()) {
			pClipboardIngredient[0] = recipe.getInput(this.name);
		}
	}

	@Override
	public void keyPressed(char character) {}

	@Override
	public void keyReleased(int keyCode) {}
}
