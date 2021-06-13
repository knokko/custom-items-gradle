package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public abstract class ModelValuesCollectionEdit<T extends ModelValues> extends GuiMenu {

	protected final GuiComponent returnMenu;
	protected final List<T> currentCollection;
	protected final Consumer<Collection<T>> onApply;

	protected final ItemList itemList;
	protected final DynamicTextComponent errorComponent;

	public ModelValuesCollectionEdit(
			GuiComponent returnMenu, Collection<T> original, Consumer<Collection<T>> onApply
	) {
		this.returnMenu = returnMenu;
		this.currentCollection = Mutability.createShallowCopy(original);
		this.onApply = onApply;

		this.itemList = new ItemList();
		this.errorComponent = new DynamicTextComponent("", ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () ->
			state.getWindow().setMainComponent(returnMenu)
		), 0.025f, 0.7f, 0.175f, 0.8f);
		addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
			onApply.accept(currentCollection);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.1f, 0.175f, 0.2f);
		addComponent(itemList, 0.25f, 0f, 1f, 0.9f);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
	
	@Override
	public void init() {
		if(didInit) itemList.refresh();
		super.init();
		errorComponent.setText("");
	}
	
	protected abstract String getItemLabel(T item);
	
	protected abstract BufferedImage getItemIcon(T item);
	
	protected abstract EditMode getEditMode(T item);
	
	protected abstract GuiComponent createEditMenu(T originalItem, Consumer<T> applyChanges);
	
	protected abstract String canDeleteItem(T itemToDelete);
	
	protected abstract CopyMode getCopyMode(T item);
	
	protected abstract GuiComponent createCopyMenu(T itemToCopy);
	
	protected class ItemList extends GuiMenu {

		@Override
		protected void addComponents() {
			float minY = 0.9f;
			
			boolean hasIcon = false;
			for (T item : currentCollection)
				if (getItemIcon(item) != null)
					hasIcon = true;
			
			float minTextX = hasIcon ? 0.15f : 0f;
			GuiTextureLoader textureLoader = state.getWindow().getTextureLoader();

			for (int index = 0; index < currentCollection.size(); index++) {
				int rememberIndex = index;
			    T item = currentCollection.get(index);

				float maxY = minY + 0.1f;
				BufferedImage icon = getItemIcon(item);
				if (icon != null)
					addComponent(new SimpleImageComponent(textureLoader.loadTexture(icon)), 0f, minY, 0.125f, maxY);
				
				String label = getItemLabel(item);
				
				addComponent(new DynamicTextComponent(label, LABEL), 
						minTextX, minY, Math.min(0.6f, minTextX + 0.05f * label.length()), maxY);
				
				if (getEditMode(item) == EditMode.SEPARATE_MENU) {
					addComponent(new DynamicTextButton("Edit", BUTTON, HOVER, () ->
						state.getWindow().setMainComponent(createEditMenu(item, newItem ->
							currentCollection.set(rememberIndex, newItem)
						))
					), 0.61f, minY, 0.72f, maxY);
				}

				CopyMode copyMode = getCopyMode(item);
				if (copyMode != CopyMode.DISABLED) {
					addComponent(new DynamicTextButton("Copy", BUTTON, HOVER, () -> {
						if (copyMode == CopyMode.SEPARATE_MENU) {
							state.getWindow().setMainComponent(createCopyMenu(item));
						} else if (copyMode == CopyMode.INSTANT) {
							T copied = Mutability.copy(item, false);
							currentCollection.add(copied);
							refresh();
						}
					}), 0.73f, minY, 0.83f, maxY);
				}
				addComponent(new DynamicTextButton("Delete", QUIT_BASE, QUIT_HOVER, () -> {
					String error = canDeleteItem(item);
					if (error == null) {
						currentCollection.remove(item);
						refresh();
					} else {
						errorComponent.setText(error);
					}
				}), 0.84f, minY, 0.99f, maxY);
				minY -= 0.1f;
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return BACKGROUND2;
		}
		
		public void refresh() {
			clearComponents();
			addComponents();
		}
	}
	
	public enum EditMode {
		
		DISABLED,
		SEPARATE_MENU
	}
	
	public enum CopyMode {
		
		DISABLED,
		INSTANT,
		SEPARATE_MENU
	}
}
