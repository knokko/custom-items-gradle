package nl.knokko.customitems.editor.menu.edit.texture;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.BowTextureEntry;
import nl.knokko.customitems.texture.BowTextureValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class BowTextureEdit extends GuiMenu {

	protected final ItemSet set;
	protected final GuiComponent returnMenu;

	final PullTextures pullTextures;
	protected final DynamicTextComponent errorComponent;

	private final TextureReference toModify;
	private final BowTextureValues currentValues;

	public BowTextureEdit(EditMenu menu, TextureReference toModify, BowTextureValues oldValues) {
		this(menu.getSet(), menu.getTextureOverview(), toModify, oldValues);
	}

	public BowTextureEdit(ItemSet set, GuiComponent returnMenu, TextureReference toModify, BowTextureValues oldValues) {
		this.set = set;
		this.returnMenu = returnMenu;

		this.pullTextures = new PullTextures();
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);

		this.toModify = toModify;
		this.currentValues = oldValues.copy(true);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 0.975f);

		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);

		addComponent(pullTextures, 0.65f, 0.025f, 0.95f, 0.775f);

		WrapperComponent<SimpleImageComponent> defaultTexture = new WrapperComponent<>(null);
		EagerTextEditField nameField = new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName);
		addComponent(
				new DynamicTextComponent("Base texture: ", LABEL),
				0.2f, 0.55f, 0.4f, 0.65f
		);
		addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(this, chosenTexture -> {
			defaultTexture.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(chosenTexture.getImage())));
			currentValues.setImage(chosenTexture.getImage());
			if (nameField.getText().isEmpty()) {
				nameField.setText(chosenTexture.getName());
			}
		}), errorComponent), 0.425f, 0.55f, 0.525f, 0.65f);
		addComponent(
				new DynamicTextComponent("Name: ", LABEL),
				0.2f, 0.4f, 0.325f, 0.5f
		);
		addComponent(nameField, 0.35f, 0.4f, 0.6f, 0.5f);

		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {

			List<BowTextureEntry> pulls = currentValues.getPullTextures();
			Set<Double> pullValues = new HashSet<>();
			for (BowTextureEntry pullEntry : pulls) {
				if (pullValues.contains(pullEntry.getPull())) {
					errorComponent.setText("Pull value " + pullEntry.getPull() + " occurs more than once");
					return;
				}
				pullValues.add(pullEntry.getPull());
			}
			pulls.sort(Comparator.comparingDouble(BowTextureEntry::getPull));
			currentValues.setPullTextures(pulls);

			String error;
			if (toModify == null) error = Validation.toErrorString(() -> set.textures.add(currentValues));
			else error = Validation.toErrorString(() -> set.textures.change(toModify, currentValues));

			if (error == null) state.getWindow().setMainComponent(returnMenu);
			else errorComponent.setText(error);
		}), 0.1f, 0.1f, 0.25f, 0.2f);

		addComponent(defaultTexture, 0.54f, 0.55f, 0.64f, 0.65f);
		addComponent(new DynamicTextButton("Add pull", BUTTON, HOVER, () -> {
			List<BowTextureEntry> pullTextureValues = currentValues.getPullTextures();
			pullTextureValues.add(BowTextureEntry.createQuick(null, 0.3));
			currentValues.setPullTextures(pullTextureValues);
			pullTextures.refreshPullComponents();
		}), 0.3f, 0.1f, 0.45f, 0.2f);
		
		HelpButtons.addHelpLink(this, "edit menu/textures/bow edit.html");
	}

	private class PullTextures extends GuiMenu {

		protected void refreshPullComponents() {
			clearComponents();
			List<BowTextureEntry> pulls = currentValues.getPullTextures();
			for (int index = 0; index < pulls.size(); index++) {
				addComponent(new PullTexture(index), 0f, 0.9f - index * 0.125f, 1f, 1f - index * 0.125f);
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}

		@Override
		protected void addComponents() {
			refreshPullComponents();
		}
	}
	
	private class PullTexture extends GuiMenu {
		
		private final int index;
		
		private PullTexture(int index) {
			this.index = index;
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}

		@Override
		protected void addComponents() {
			BowTextureEntry oldEntry = currentValues.getPullTextures().get(this.index);
			addComponent(
					new DynamicTextComponent("Pull: ", LABEL),
					0.05f, 0.5f, 0.3f, 0.9f
			);
			addComponent(new EagerFloatEditField(oldEntry.getPull(), 0.0, EDIT_BASE, EDIT_ACTIVE, newPull -> {
				List<BowTextureEntry> entries = currentValues.getPullTextures();
				BowTextureEntry newEntry = entries.get(index).copy(true);
				newEntry.setPull(newPull);
				entries.set(index, newEntry);
				currentValues.setPullTextures(entries);
			}), 0.3f, 0.5f, 0.6f, 0.9f);

			GuiTextureLoader loader = state.getWindow().getTextureLoader();
			addComponent(new ImageButton(loader.loadTexture("nl/knokko/gui/images/icons/delete.png"), 
					loader.loadTexture("nl/knokko/gui/images/icons/delete_hover.png"), () -> {
				List<BowTextureEntry> pulls = currentValues.getPullTextures();
				pulls.remove(index);
				currentValues.setPullTextures(pulls);
				pullTextures.refreshPullComponents();
			}), 0.875f, 0.5f, 0.975f, 0.9f);

			addComponent(
					new DynamicTextComponent("Texture: ", EditProps.LABEL),
					0.05f, 0.05f, 0.5f, 0.45f
			);
			WrapperComponent<SimpleImageComponent> imageWrapper;
			if (oldEntry.getImage() == null)
				imageWrapper = new WrapperComponent<>(null);
			else
				imageWrapper = new WrapperComponent<>(new SimpleImageComponent(loader.loadTexture(oldEntry.getImage())));
			addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(BowTextureEdit.this, chosenTexture -> {
				List<BowTextureEntry> pulls = currentValues.getPullTextures();
				BowTextureEntry newEntry = pulls.get(index).copy(true);
				newEntry.setImage(chosenTexture.getImage());
				pulls.set(index, newEntry);
				currentValues.setPullTextures(pulls);
				imageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(chosenTexture.getImage())));
			}), errorComponent), 0.5f, 0.05f, 0.75f, 0.45f);
			addComponent(imageWrapper, 0.75f, 0.55f, 0.85f, 0.9f);
		}
	}
}
