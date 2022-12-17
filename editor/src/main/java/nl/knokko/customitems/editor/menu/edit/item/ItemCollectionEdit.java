package nl.knokko.customitems.editor.menu.edit.item;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.commandhelp.CommandBlockHelpOverview;
import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.item.equipment.EquipmentSetCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class ItemCollectionEdit extends DedicatedCollectionEdit<CustomItemValues, ItemReference> {
	
	private final EditMenu menu;

	public ItemCollectionEdit(EditMenu menu) {
		super(menu, menu.getSet().getItems().references(), null);
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create item", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateItem(menu));
		}), 0.025f, 0.3f, 0.225f, 0.4f);
		addComponent(new DynamicTextButton("Equipment sets", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EquipmentSetCollectionEdit(menu));
		}), 0.025f, 0.175f, 0.23f, 0.275f);
		addComponent(new DynamicTextButton("Command block help", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CommandBlockHelpOverview(menu.getSet(), this));
		}), 0.025f, 0.05f, 0.275f, 0.15f);

		HelpButtons.addHelpLink(this, "edit menu/items/overview.html");
	}

	private GuiComponent createEditMenu(ItemReference itemReference, boolean copy) {
		ItemReference toModify = copy ? null : itemReference;
		CustomItemValues itemValues = itemReference.get();

		if (itemValues instanceof CustomBowValues)
			return new EditItemBow(menu, (CustomBowValues) itemValues, toModify);
		else if (itemValues instanceof CustomCrossbowValues)
			return new EditItemCrossbow(menu, (CustomCrossbowValues) itemValues, toModify);
		else if (itemValues instanceof CustomHelmet3dValues)
			return new EditItemHelmet3D(menu, (CustomHelmet3dValues) itemValues, toModify);
		else if (itemValues instanceof CustomElytraValues)
			return new EditItemElytra(menu, (CustomElytraValues) itemValues, toModify);
		else if (itemValues instanceof CustomArmorValues)
			return new EditItemArmor<>(menu, (CustomArmorValues) itemValues, toModify);
		else if (itemValues instanceof CustomShearsValues)
			return new EditItemShears(menu, (CustomShearsValues) itemValues, toModify);
		else if (itemValues instanceof CustomHoeValues)
			return new EditItemHoe(menu, (CustomHoeValues) itemValues, toModify);
		else if (itemValues instanceof CustomShieldValues)
			return new EditItemShield(menu, (CustomShieldValues) itemValues, toModify);
		else if (itemValues instanceof CustomTridentValues)
			return new EditItemTrident(menu, (CustomTridentValues) itemValues, toModify);
		else if (itemValues instanceof CustomToolValues)
			return new EditItemTool<>(menu, (CustomToolValues) itemValues, toModify);
		else if (itemValues instanceof CustomWandValues)
			return new EditItemWand(menu, (CustomWandValues) itemValues, toModify);
		else if (itemValues instanceof CustomGunValues)
			return new EditItemGun(menu, (CustomGunValues) itemValues, toModify);
		else if (itemValues instanceof CustomPocketContainerValues)
			return new EditItemPocketContainer(menu, (CustomPocketContainerValues) itemValues, toModify);
		else if (itemValues instanceof SimpleCustomItemValues)
			return new EditItemSimple(menu, (SimpleCustomItemValues) itemValues, toModify);
		else if (itemValues instanceof CustomFoodValues)
			return new EditItemFood(menu, (CustomFoodValues) itemValues, toModify);
		else if (itemValues instanceof CustomArrowValues)
			return new EditItemArrow(menu, (CustomArrowValues) itemValues, toModify);
		else if (itemValues instanceof CustomBlockItemValues)
			return new EditItemBlock(menu, (CustomBlockItemValues) itemValues, toModify);
		else if (itemValues instanceof CustomMusicDiscValues)
			return new EditItemMusicDisc(menu, (CustomMusicDiscValues) itemValues, toModify);
		else
			throw new IllegalArgumentException("Unsupported custom item class: " + itemValues.getClass());
	}

	@Override
	public GuiComponent createEditMenu(ItemReference item) {
		return createEditMenu(item, false);
	}

	@Override
	public GuiComponent createCopyMenu(ItemReference item) {
		return createEditMenu(item, true);
	}

	@Override
	public String deleteModel(ItemReference itemToDelete) {
		return Validation.toErrorString(() -> menu.getSet().removeItem(itemToDelete));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ItemReference modelReference) {
		return CopyMode.SEPARATE_MENU;
	}

	@Override
	public String getModelLabel(CustomItemValues item) {
		return item.getName();
	}

	@Override
	public BufferedImage getModelIcon(CustomItemValues item) {
		return item.getTexture().getImage();
	}

	@Override
	protected boolean canEditModel(CustomItemValues model) {
		return true;
	}
}
