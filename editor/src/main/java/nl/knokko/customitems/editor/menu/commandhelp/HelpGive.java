package nl.knokko.customitems.editor.menu.commandhelp;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectCustomItem;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class HelpGive extends GuiMenu {

	private final ItemSet set;
	private final GuiComponent returnMenu;

	private final WrapperComponent<SimpleImageComponent> selectedItemImage;
	private final DynamicTextComponent infoComponent;
	private CustomItem selectedItem;

	public HelpGive(ItemSet set, GuiComponent returnMenu) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.selectedItemImage = new WrapperComponent<SimpleImageComponent>(null);
		this.infoComponent = new DynamicTextComponent("", EditProps.LABEL);
		this.selectedItem = null;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.175f, 0.9f);
		addComponent(infoComponent, 0.15f, 0.9f, 0.85f, 1f);
		addComponent(new DynamicTextComponent("First select an item, then click on one of the generate buttons below.",
				EditProps.LABEL), 0.1f, 0.6f, 0.9f, 0.7f);
		addComponent(
				new DynamicTextComponent("Thereafter, the command will be copied to your clipboard.", EditProps.LABEL),
				0.1f, 0.5f, 0.7f, 0.6f);
		addComponent(
				new DynamicTextComponent("Then you can paste it in a command block by holding control and pressing v.",
						EditProps.LABEL),
				0.1f, 0.4f, 0.95f, 0.5f);
		addComponent(selectedItemImage, 0.55f, 0.8f, 0.65f, 0.9f);
		addComponent(new DynamicTextButton("Select item...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (CustomItem chosen) -> {
				selectedItem = chosen;
				selectedItemImage.setComponent(new SimpleImageComponent(
						state.getWindow().getTextureLoader().loadTexture(chosen.getTexture().getImage())));
			}, set));
		}), 0.7f, 0.8f, 0.85f, 0.9f);
		addComponent(new DynamicTextButton("Generate for minecraft 1.12", EditProps.BUTTON, EditProps.HOVER, () -> {
			String command = "/give @p stick 1 0 {KnokkosCustomItems:{Name:" + selectedItem.getName() + "}}";
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
			String command = "/give @p stick{KnokkosCustomItems:{Name:" + selectedItem.getName() + "}}";
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

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}