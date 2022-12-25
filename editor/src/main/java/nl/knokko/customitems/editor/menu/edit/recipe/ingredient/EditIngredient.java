package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.constraint.EditIngredientConstraints;
import nl.knokko.customitems.editor.menu.edit.recipe.result.*;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.recipe.result.*;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.ConditionalTextComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class EditIngredient extends GuiMenu {
	
	private final Consumer<IngredientValues> listener;
	private final GuiComponent returnMenu;
	private final ItemSet set;
	private final boolean allowEmpty;
	private final IngredientValues currentIngredient;

	public EditIngredient(
			GuiComponent returnMenu, Consumer<IngredientValues> listener,
			IngredientValues oldIngredient, boolean allowEmpty, ItemSet set
	) {
		this.listener = listener;
		this.returnMenu = returnMenu;
		this.allowEmpty = allowEmpty;
		this.set = set;
		this.currentIngredient = oldIngredient.copy(true);
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.7f, 0.15f, 0.8f);

		addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
			listener.accept(currentIngredient);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.2f, 0.15f, 0.3f);

		DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);

		DynamicTextComponent remainingItemDescription = new DynamicTextComponent("", LABEL);
		Runnable updateRemainingItemDescription = () -> {
			if (currentIngredient instanceof NoIngredientValues) {
				remainingItemDescription.setText("");
				return;
			}

			ResultValues remainingItem = currentIngredient.getRemainingItem();
			if (remainingItem == null) {
				remainingItemDescription.setText("Currently none");
			} else if (remainingItem instanceof CopiedResultValues) {
				remainingItemDescription.setText("Currently copied from server");
			} else if (remainingItem instanceof CustomItemResultValues) {
				remainingItemDescription.setText("Currently " + ((CustomItemResultValues) remainingItem).getItem());
			} else if (remainingItem instanceof DataVanillaResultValues) {
				DataVanillaResultValues dataRemaining = (DataVanillaResultValues) remainingItem;
				remainingItemDescription.setText("Currently " + dataRemaining.getMaterial() + " [" + dataRemaining.getDataValue() + "]");
			} else if (remainingItem instanceof ItemBridgeResultValues) {
				remainingItemDescription.setText("Currently ItemBridge(" + ((ItemBridgeResultValues) remainingItem).getItemId() + ")");
			} else if (remainingItem instanceof MimicResultValues) {
				remainingItemDescription.setText("Currently Mimic(" + ((MimicResultValues) remainingItem).getItemId() + ")");
			} else if (remainingItem instanceof SimpleVanillaResultValues) {
				remainingItemDescription.setText("Currently " + ((SimpleVanillaResultValues) remainingItem).getMaterial());
			} else {
				remainingItemDescription.setText("Programming error: unknown remaining item");
			}
		};

		addComponent(new ConditionalTextComponent(
				"Remaining item:", EditProps.LABEL,
						() -> !(currentIngredient instanceof NoIngredientValues)),
				0.15f, 0.15f, 0.34f, 0.25f);
		addComponent(new ConditionalTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseResult(
					this, newRemainingItem -> {
						currentIngredient.setRemainingItem(newRemainingItem);
						updateRemainingItemDescription.run();
					}, set
			));
		}, () -> !(currentIngredient instanceof NoIngredientValues)
		), 0.35f, 0.15f, 0.45f, 0.25f);
		addComponent(new ConditionalTextButton("Clear", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			currentIngredient.setRemainingItem(null);
		}, () -> !(currentIngredient instanceof NoIngredientValues)), 0.475f, 0.15f, 0.575f, 0.25f);
		addComponent(remainingItemDescription, 0.6f, 0.15f, 0.975f, 0.25f);
		updateRemainingItemDescription.run();

		addComponent(new ConditionalTextButton("Constraints...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EditIngredientConstraints(
					this, currentIngredient::setConstraints, currentIngredient.getConstraints()
			));
		}, () -> !(currentIngredient instanceof NoIngredientValues)), 0.2f, 0.025f, 0.4f, 0.125f);

		String currentItemDescription = "Currently ";
		if (currentIngredient instanceof CustomItemIngredientValues) {
			currentItemDescription += ((CustomItemIngredientValues) currentIngredient).getItem().getName();
		} else if (currentIngredient instanceof DataVanillaIngredientValues) {
			DataVanillaIngredientValues dataIngredient = (DataVanillaIngredientValues) currentIngredient;
			currentItemDescription += dataIngredient.getMaterial() + " [" + dataIngredient.getDataValue() + "]";
		} else if (currentIngredient instanceof ItemBridgeIngredientValues) {
			currentItemDescription += "ItemBridge(" + ((ItemBridgeIngredientValues) currentIngredient).getItemId() + ")";
		} else if (currentIngredient instanceof MimicIngredientValues) {
			currentItemDescription += "Mimic(" + ((MimicIngredientValues) currentIngredient).getItemId() + ")";
		} else if (currentIngredient instanceof NoIngredientValues) {
			currentItemDescription += "empty";
		} else if (currentIngredient instanceof SimpleVanillaIngredientValues) {
			currentItemDescription += ((SimpleVanillaIngredientValues) currentIngredient).getMaterial();
		} else {
			currentItemDescription += "Unknown ingredient type: programming error";
		}
		addComponent(new DynamicTextComponent(currentItemDescription, LABEL), 0.25f, 0.8f, 0.6f, 0.9f);
		addComponent(
				new DynamicTextComponent("Change to custom item...", LABEL),
				0.175f, 0.7f, 0.4f, 0.8f
		);
		addComponent(new DynamicTextButton("from this plug-in", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseCustomResult(returnMenu, customResult -> {
				listener.accept(CustomItemIngredientValues.createQuick(
						customResult.getItemReference(), customResult.getAmount(),
						currentIngredient.getRemainingItem(), currentIngredient.getConstraints()
				));
				state.getWindow().setMainComponent(returnMenu);
			}, set));
		}), 0.175f, 0.57f, 0.3f, 0.67f);
		addComponent(new DynamicTextButton("from another plug-in with Mimic integration", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseMimicResult(returnMenu, mimicResult -> {
				listener.accept(MimicIngredientValues.createQuick(
						mimicResult.getItemId(), mimicResult.getAmount(),
						currentIngredient.getRemainingItem(), currentIngredient.getConstraints()
				));
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.175f, 0.46f, 0.5f, 0.56f);
		addComponent(new DynamicTextButton("from another plug-in with ItemBridge integration", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseItemBridgeResult(returnMenu, itemBridgeResult -> {
				listener.accept(ItemBridgeIngredientValues.createQuick(
						itemBridgeResult.getItemId(), itemBridgeResult.getAmount(),
						currentIngredient.getRemainingItem(), currentIngredient.getConstraints()
				));
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.175f, 0.35f, 0.525f, 0.45f);

		addComponent(new DynamicTextComponent("Change to vanilla item...", LABEL), 0.55f, 0.7f, 0.75f, 0.8f);
		addComponent(new DynamicTextButton("simple", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseSimpleVanillaResult(returnMenu, vanillaResult -> {
				listener.accept(SimpleVanillaIngredientValues.createQuick(
						vanillaResult.getMaterial(), vanillaResult.getAmount(),
						currentIngredient.getRemainingItem(), currentIngredient.getConstraints()
				));
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.55f, 0.57f, 0.65f, 0.67f);
		addComponent(new DynamicTextButton("with data value (1.12)", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseDataVanillaResult(returnMenu, true, vanillaResult -> {
				listener.accept(DataVanillaIngredientValues.createQuick(
						vanillaResult.getMaterial(), vanillaResult.getDataValue(), vanillaResult.getAmount(),
						currentIngredient.getRemainingItem(), currentIngredient.getConstraints()
				));
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.55f, 0.46f, 0.775f, 0.56f);

		if (allowEmpty) {
			addComponent(new DynamicTextButton("Set empty", EditProps.BUTTON, EditProps.HOVER, () -> {
				listener.accept(new NoIngredientValues());
				state.getWindow().setMainComponent(returnMenu);
			}), 0.775f, 0.7f, 0.975f, 0.8f);
		}

		HelpButtons.addHelpLink(this, "edit menu/recipes/edit ingredient.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}