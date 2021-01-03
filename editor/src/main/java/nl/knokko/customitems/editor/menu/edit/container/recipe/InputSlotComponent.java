package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.ChooseIngredient;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class InputSlotComponent implements GuiComponent {
	
	private static final GuiColor BASE_COLOR = new SimpleGuiColor(0, 0, 200);
	private static final GuiColor HOVER_COLOR = new SimpleGuiColor(100, 100, 200);
	
	private final String name;
	private final GuiComponent outerMenu;
	private final Collection<InputEntry> inputs;
	private final ItemSet set;
	
	private GuiComponentState state;
	private GuiTexture topTextTexture;
	private GuiTexture bottomTextTexture;
	
	public InputSlotComponent(String name, GuiComponent outerMenu, 
			Collection<InputEntry> inputs, ItemSet set) {
		this.name = name;
		this.outerMenu = outerMenu;
		this.inputs = inputs;
		this.set = set;
	}
	
	public String getName() {
		return name;
	}
	
	private InputEntry getOwnEntry() {
		for (InputEntry candidate : inputs) {
			if (candidate.getInputSlotName().equals(name)) {
				return candidate;
			}
		}
		
		return null;
	}
	
	private Ingredient getOwnIngredient() {
		InputEntry entry = getOwnEntry();
		if (entry != null) {
			return (Ingredient) entry.getIngredient();
		} else {
			return null;
		}
	}
	
	private void setIngredient(Ingredient newIngredient) {
		
		// Make sure only null indicates that there is no ingredient
		if (newIngredient instanceof NoIngredient) {
			newIngredient = null;
		}
		
		// Update inputs collection
		InputEntry currentEntry = getOwnEntry();
		if (currentEntry != null) {
			inputs.remove(currentEntry);
		}
		if (newIngredient != null) {
			inputs.add(new InputEntry(name, newIngredient));
		}
		
		// Update text
		String bottomText = newIngredient == null ? "" : newIngredient.toString("");
		
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
				TextBuilder.createTexture("input", EditProps.LABEL)
		);
		this.setIngredient(this.getOwnIngredient());
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
		state.getWindow().setMainComponent(new ChooseIngredient(outerMenu, 
				this::setIngredient, true, set
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
