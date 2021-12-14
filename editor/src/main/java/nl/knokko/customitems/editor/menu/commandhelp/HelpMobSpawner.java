package nl.knokko.customitems.editor.menu.commandhelp;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectCustomItem;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.commandhelp.HelpSummon.getEquipmentTag;

public class HelpMobSpawner extends GuiMenu {

	private final SItemSet set;
	private final GuiComponent returnMenu;

	private CustomItemValues selectedMainHand, selectedOffHand, selectedHelmet, selectedChestplate, selectedLeggings,
			selectedBoots;

	public HelpMobSpawner(SItemSet set, GuiComponent returnMenu) {
		this.set = set;
		this.returnMenu = returnMenu;
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		TextComponent infoComponent = new TextComponent("", EditProps.LABEL);
		WrapperComponent<SimpleImageComponent> mainHandImage = new WrapperComponent<>(null);
		WrapperComponent<SimpleImageComponent> offHandImage = new WrapperComponent<>(null);
		WrapperComponent<SimpleImageComponent> helmetImage = new WrapperComponent<>(null);
		WrapperComponent<SimpleImageComponent> chestplateImage = new WrapperComponent<>(null);
		WrapperComponent<SimpleImageComponent> leggingsImage = new WrapperComponent<>(null);
		WrapperComponent<SimpleImageComponent> bootsImage = new WrapperComponent<>(null);

		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.175f, 0.9f);
		addComponent(infoComponent, 0.025f, 0.9f, 0.975f, 1f);
		addComponent(
				new DynamicTextComponent("First select the equipment, then click on one of the generate buttons below.",
						EditProps.LABEL),
				0.01f, 0.6f, 0.75f, 0.7f);
		addComponent(
				new DynamicTextComponent("Thereafter, the command will be copied to your clipboard.", EditProps.LABEL),
				0.01f, 0.5f, 0.6f, 0.6f);
		addComponent(
				new DynamicTextComponent("Then you can paste it in a command block by holding control and pressing v.",
						EditProps.LABEL),
				0.01f, 0.4f, 0.75f, 0.5f);

		// The select buttons + their images
		addComponent(new DynamicTextButton("Select maind hand...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (ItemReference chosen) -> {
				selectedMainHand = chosen.get();
				mainHandImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(selectedMainHand.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.8f, 0.9f, 0.9f);
		addComponent(mainHandImage, 0.9f, 0.8f, 1f, 0.9f);
		addComponent(new DynamicTextButton("Select off hand...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (ItemReference chosen) -> {
				selectedOffHand = chosen.get();
				offHandImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(selectedOffHand.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.675f, 0.9f, 0.775f);
		addComponent(offHandImage, 0.9f, 0.675f, 1f, 0.775f);
		addComponent(new DynamicTextButton("Select helmet...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (ItemReference chosen) -> {
				selectedHelmet = chosen.get();
				helmetImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(selectedHelmet.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.55f, 0.9f, 0.65f);
		addComponent(helmetImage, 0.9f, 0.55f, 1f, 0.65f);
		addComponent(new DynamicTextButton("Select chestplate...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (ItemReference chosen) -> {
				selectedChestplate = chosen.get();
				chestplateImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(selectedChestplate.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.425f, 0.9f, 0.525f);
		addComponent(chestplateImage, 0.9f, 0.425f, 1f, 0.525f);
		addComponent(new DynamicTextButton("Select leggings...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (ItemReference chosen) -> {
				selectedLeggings = chosen.get();
				leggingsImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(selectedLeggings.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.3f, 0.9f, 0.4f);
		addComponent(leggingsImage, 0.9f, 0.3f, 1f, 0.4f);
		addComponent(new DynamicTextButton("Select boots...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (ItemReference chosen) -> {
				selectedBoots = chosen.get();
				bootsImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(selectedBoots.getTexture().getImage())));
			}, set));
		}), 0.75f, 0.175f, 0.9f, 0.275f);
		addComponent(bootsImage, 0.9f, 0.175f, 1f, 0.275f);

		addComponent(new DynamicTextButton("Generate for minecraft 1.12", EditProps.BUTTON, EditProps.HOVER, () -> {
			String command = "/setblock ~ ~1 ~ mob_spawner 0 replace {SpawnData:{id:skeleton,HandItems:["
						+ getEquipmentTag(selectedMainHand) + "," + getEquipmentTag(selectedOffHand)
						+ "],ArmorItems:[" + getEquipmentTag(selectedBoots) + "," + getEquipmentTag(selectedLeggings)
						+ "," + getEquipmentTag(selectedChestplate) + "," + getEquipmentTag(selectedHelmet) + "]},Delay:2}";
			String error = CommandBlockHelpOverview.setClipboard(command);
			if (error == null) {
				infoComponent.setProperties(EditProps.LABEL);
				infoComponent.setText("Copied command to clipboard");
			} else {
				infoComponent.setProperties(EditProps.ERROR);
				infoComponent.setText("Could not copy command to clipboard because: " + error);
			}
		}), 0.2f, 0.05f, 0.45f, 0.15f);
		addComponent(new DynamicTextButton("Generate for minecraft 1.13+", EditProps.BUTTON, EditProps.HOVER, () -> {
			String command = "/setblock ~ ~1 ~ spawner{SpawnData:{id:skeleton,HandItems:["
						+ getEquipmentTag(selectedMainHand) + "," + getEquipmentTag(selectedOffHand)
						+ "],ArmorItems:[" + getEquipmentTag(selectedBoots) + "," + getEquipmentTag(selectedLeggings)
						+ "," + getEquipmentTag(selectedChestplate) + ","
						+ getEquipmentTag(selectedHelmet) + "]},Delay:2} replace";
			String error = CommandBlockHelpOverview.setClipboard(command);
			if (error == null) {
				infoComponent.setProperties(EditProps.LABEL);
				infoComponent.setText("Copied command to clipboard");
			} else {
				infoComponent.setProperties(EditProps.ERROR);
				infoComponent.setText("Could not copy command to clipboard because: " + error);
			}
		}), 0.55f, 0.05f, 0.8f, 0.15f);
	}
}
