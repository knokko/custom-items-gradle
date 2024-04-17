package nl.knokko.customitems.editor.menu.edit.item;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.commandhelp.CommandBlockHelpOverview;
import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.item.damage.DamageSourceCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.item.equipment.EquipmentSetCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class ItemCollectionEdit extends DedicatedCollectionEdit<KciItem, ItemReference> {
	
	private final EditMenu menu;

	public ItemCollectionEdit(EditMenu menu) {
		super(menu, menu.getSet().items.references(), null);
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create item", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateItem(menu));
		}), 0.025f, 0.385f, 0.225f, 0.485f);
		addComponent(new DynamicTextButton("Custom damage sources", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new DamageSourceCollectionEdit(this, menu.getSet()));
		}), 0.025f, 0.26f, 0.275f, 0.36f);
		addComponent(new DynamicTextButton("Equipment sets", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EquipmentSetCollectionEdit(menu));
		}), 0.025f, 0.135f, 0.23f, 0.235f);
		addComponent(new DynamicTextButton("Command block help", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CommandBlockHelpOverview(menu.getSet(), this));
		}), 0.025f, 0.01f, 0.275f, 0.11f);

		HelpButtons.addHelpLink(this, "edit menu/items/overview.html");
	}

	private GuiComponent createEditMenu(ItemReference itemReference, boolean copy) {
		ItemReference toModify = copy ? null : itemReference;
		KciItem itemValues = itemReference.get();

		if (itemValues instanceof KciBow)
			return new EditItemBow(menu, (KciBow) itemValues, toModify);
		else if (itemValues instanceof KciCrossbow)
			return new EditItemCrossbow(menu, (KciCrossbow) itemValues, toModify);
		else if (itemValues instanceof Kci3dHelmet)
			return new EditItemHelmet3D(menu, (Kci3dHelmet) itemValues, toModify);
		else if (itemValues instanceof KciElytra)
			return new EditItemElytra(menu, (KciElytra) itemValues, toModify);
		else if (itemValues instanceof KciArmor)
			return new EditItemArmor<>(menu, (KciArmor) itemValues, toModify);
		else if (itemValues instanceof KciShears)
			return new EditItemShears(menu, (KciShears) itemValues, toModify);
		else if (itemValues instanceof KciHoe)
			return new EditItemHoe(menu, (KciHoe) itemValues, toModify);
		else if (itemValues instanceof KciShield)
			return new EditItemShield(menu, (KciShield) itemValues, toModify);
		else if (itemValues instanceof KciTrident)
			return new EditItemTrident(menu, (KciTrident) itemValues, toModify);
		else if (itemValues instanceof KciTool)
			return new EditItemTool<>(menu, (KciTool) itemValues, toModify);
		else if (itemValues instanceof KciWand)
			return new EditItemWand(menu, (KciWand) itemValues, toModify);
		else if (itemValues instanceof KciGun)
			return new EditItemGun(menu, (KciGun) itemValues, toModify);
		else if (itemValues instanceof KciThrowable)
			return new EditItemThrowable(menu, (KciThrowable) itemValues, toModify);
		else if (itemValues instanceof KciPocketContainer)
			return new EditItemPocketContainer(menu, (KciPocketContainer) itemValues, toModify);
		else if (itemValues instanceof KciSimpleItem)
			return new EditItemSimple(menu, (KciSimpleItem) itemValues, toModify);
		else if (itemValues instanceof KciFood)
			return new EditItemFood(menu, (KciFood) itemValues, toModify);
		else if (itemValues instanceof KciArrow)
			return new EditItemArrow(menu, (KciArrow) itemValues, toModify);
		else if (itemValues instanceof KciBlockItem)
			return new EditItemBlock(menu, (KciBlockItem) itemValues, toModify);
		else if (itemValues instanceof KciMusicDisc)
			return new EditItemMusicDisc(menu, (KciMusicDisc) itemValues, toModify);
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
		return Validation.toErrorString(() -> menu.getSet().items.remove(itemToDelete));
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
	public String getModelLabel(KciItem item) {
		return item.getName();
	}

	@Override
	public BufferedImage getModelIcon(KciItem item) {
		return item.getTexture().getImage();
	}

	@Override
	protected boolean canEditModel(KciItem model) {
		return true;
	}
}
