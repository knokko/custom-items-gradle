package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.CHOOSE_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.CHOOSE_HOVER;

public class CreateItem extends GuiMenu {

	private final ItemSet itemSet;
	private final GuiComponent returnMenu;

	public CreateItem(ItemSet itemSet, GuiComponent returnMenu) {
		this.itemSet = itemSet;
		this.returnMenu = returnMenu;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.8f, 0.25f, 0.9f);

		addComponent(new DynamicTextButton("Food or Potion", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemFood(itemSet, returnMenu, new KciFood(true), null));
		}), 0.5f, 0.89f, 0.7f, 0.99f);
		addComponent(new DynamicTextButton("Simple Item", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemSimple(itemSet, returnMenu, new KciSimpleItem(true), null));
		}), 0.5f, 0.78f, 0.7f, 0.88f);

		// The row for later minecraft versions
		addComponent(new DynamicTextButton("Trident (1.13 and 1.14)", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemTrident(itemSet, returnMenu, new KciTrident(true), null));
		}), 0f, 0.65f, 0.23f, 0.75f);
		addComponent(new DynamicTextButton("Crossbow (1.14+)", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemCrossbow(itemSet, returnMenu, new KciCrossbow(true), null));
		}), 0f, 0.525f, 0.23f, 0.625f);
		addComponent(new DynamicTextButton("Block (1.13+)", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemBlock(itemSet, returnMenu, new KciBlockItem(true), null));
		}), 0f, 0.4f, 0.2f, 0.5f);
		addComponent(new DynamicTextButton("Music disc (1.14+)", CHOOSE_BASE, CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemMusicDisc(itemSet, returnMenu, new KciMusicDisc(true), null));
		}), 0f, 0.275f, 0.23f, 0.375f);
		addComponent(new DynamicTextButton("Arrow (1.14+)", CHOOSE_BASE, CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemArrow(itemSet, returnMenu, new KciArrow(true), null));
		}), 0f, 0.15f, 0.2f, 0.25f);

		// The row for the special stuff
		addComponent(new DynamicTextButton("Wand", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemWand(itemSet, returnMenu, new KciWand(true), null));
		}), 0.25f, 0.65f, 0.45f, 0.75f);

		addComponent(new DynamicTextButton("Gun", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemGun(itemSet, returnMenu, new KciGun(true), null));
		}), 0.25f, 0.525f, 0.45f, 0.625f);

		addComponent(new DynamicTextButton("Throwable", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemThrowable(itemSet, returnMenu, new KciThrowable(true), null));
		}), 0.25f, 0.4f, 0.45f, 0.5f);

		addComponent(new DynamicTextButton("Pocket Container", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemPocketContainer(itemSet, returnMenu, new KciPocketContainer(true), null));
		}), 0.25f, 0.275f, 0.45f, 0.375f);
		
		// The row for the basic tools
		addComponent(new DynamicTextButton("Sword", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemTool<>(itemSet, returnMenu, new KciTool(true, KciItemType.IRON_SWORD), null));
		}), 0.5f, 0.65f, 0.7f, 0.75f);
		addComponent(new DynamicTextButton("Pickaxe", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemTool<>(itemSet, returnMenu, new KciTool(true, KciItemType.IRON_PICKAXE), null));
		}), 0.5f, 0.525f, 0.7f, 0.625f);
		addComponent(new DynamicTextButton("Axe", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemTool<>(itemSet, returnMenu, new KciTool(true, KciItemType.IRON_AXE), null));
		}), 0.5f, 0.4f, 0.7f, 0.5f);
		addComponent(new DynamicTextButton("Shovel", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemTool<>(itemSet, returnMenu, new KciTool(true, KciItemType.IRON_SHOVEL), null));
		}), 0.5f, 0.275f, 0.7f, 0.375f);
		addComponent(new DynamicTextButton("Hoe", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemHoe(itemSet, returnMenu, new KciHoe(true), null));
		}), 0.5f, 0.15f, 0.7f, 0.25f);
		addComponent(new DynamicTextButton("Shear", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemShears(itemSet, returnMenu, new KciShears(true), null));
		}), 0.5f, 0.025f, 0.7f, 0.125f);
		
		// The row for the advanced combat stuff
		addComponent(new DynamicTextButton("Elytra", CHOOSE_BASE, CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemElytra(itemSet, returnMenu, new KciElytra(true), null));
		}), 0.75f, 0.885f, 0.95f, 0.985f);
		addComponent(new DynamicTextButton("3D helmet", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemHelmet3D(itemSet, returnMenu, new Kci3dHelmet(true), null));
		}), 0.75f, 0.775f, 0.95f, 0.875f);
		addComponent(new DynamicTextButton("Bow", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemBow(itemSet, returnMenu, new KciBow(true), null));
		}), 0.75f, 0.65f, 0.95f, 0.75f);
		addComponent(new DynamicTextButton("Helmet", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemArmor<>(itemSet, returnMenu, new KciArmor(true, KciItemType.IRON_HELMET), null));
		}), 0.75f, 0.525f, 0.95f, 0.625f);
		addComponent(new DynamicTextButton("Chestplate", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemArmor<>(itemSet, returnMenu, new KciArmor(true, KciItemType.IRON_CHESTPLATE), null));
		}), 0.75f, 0.4f, 0.95f, 0.5f);
		addComponent(new DynamicTextButton("Leggings", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemArmor<>(itemSet, returnMenu, new KciArmor(true, KciItemType.IRON_LEGGINGS), null));
		}), 0.75f, 0.275f, 0.95f, 0.375f);
		addComponent(new DynamicTextButton("Boots", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemArmor<>(itemSet, returnMenu, new KciArmor(true, KciItemType.IRON_BOOTS), null));
		}), 0.75f, 0.15f, 0.95f, 0.25f);
		addComponent(new DynamicTextButton("Shield", CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemShield(itemSet, returnMenu, new KciShield(true), null));
		}), 0.75f, 0.025f, 0.95f, 0.125f);

		HelpButtons.addHelpLink(this, "edit menu/items/select type.html");
	}
}
