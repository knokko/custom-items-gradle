package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseResult extends GuiMenu {
	
	private final Consumer<ResultValues> listener;
	private final GuiComponent returnMenu;
	private final ItemSet set;
	private final boolean allowNull;
	private final ResultValues oldResult;
	private final BiFunction<GuiComponent, UpgradeResultValues, GuiComponent> createUpgradeIngredientMenu;
	
	public ChooseResult(
			GuiComponent returnMenu, Consumer<ResultValues> listener, ItemSet set, boolean allowNull, ResultValues oldResult,
			BiFunction<GuiComponent, UpgradeResultValues, GuiComponent> createUpgradeIngredientMenu
	) {
		this.listener = listener;
		this.returnMenu = returnMenu;
		this.set = set;
		this.allowNull = allowNull;
		this.oldResult = oldResult;
		this.createUpgradeIngredientMenu = createUpgradeIngredientMenu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.7f, 0.15f, 0.8f);

		addComponent(
				new DynamicTextComponent("Custom item...", LABEL),
				0.175f, 0.8f, 0.3f, 0.9f
		);
		addComponent(new DynamicTextButton("from this plug-in", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseCustomResult(returnMenu, listener::accept, set));
		}), 0.175f, 0.65f, 0.3f, 0.75f);
		addComponent(new DynamicTextButton("from another plug-in with Mimic integration", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseMimicResult(returnMenu, listener::accept));
		}), 0.175f, 0.5f, 0.5f, 0.6f);
		addComponent(new DynamicTextButton("from another plug-in with ItemBridge integration", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseItemBridgeResult(returnMenu, listener::accept));
		}), 0.175f, 0.35f, 0.525f, 0.45f);

		addComponent(new DynamicTextComponent("Vanilla item...", LABEL), 0.55f, 0.8f, 0.7f, 0.9f);
		addComponent(new DynamicTextButton("simple", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseSimpleVanillaResult(returnMenu, listener::accept));
		}), 0.55f, 0.65f, 0.65f, 0.75f);
		addComponent(new DynamicTextButton("with data value", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseDataVanillaResult(returnMenu, true, listener::accept));
		}), 0.55f, 0.5f, 0.75f, 0.6f);

		addComponent(new DynamicTextButton("Copied from server", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseCopyResult(this, result -> {
				listener.accept(result);
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.775f, 0.8f, 0.95f, 0.9f);
		if (createUpgradeIngredientMenu != null) {
			addComponent(new DynamicTextButton("Upgrade ingredient", BUTTON, HOVER, () -> {
				state.getWindow().setMainComponent(new ChooseUpgradeResult(
						returnMenu, set, listener,
						oldResult instanceof UpgradeResultValues ? (UpgradeResultValues) oldResult : new UpgradeResultValues(true),
						createUpgradeIngredientMenu
				));
			}), 0.775f, 0.65f, 0.95f, 0.75f);
		}
		if (allowNull) {
			addComponent(new DynamicTextButton("Choose nothing", BUTTON, HOVER, () -> {
				listener.accept(null);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.775f, 0.5f, 0.9f, 0.6f);
		}

		HelpButtons.addHelpLink(this, "edit menu/recipes/choose result.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}