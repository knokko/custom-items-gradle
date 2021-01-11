package nl.knokko.customitems.editor.menu.edit;

import java.util.Collection;
import java.util.Locale;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.simple.SimpleColorComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static java.lang.Math.min;

import java.awt.image.BufferedImage;

public class CollectionEdit<T> extends GuiMenu {
	
	private final ActionHandler<T> handler;
	private final Collection<T> changingCollection;
	
	private final List itemList;
	protected final TextEditField searchField;
	protected final DynamicTextComponent errorComponent;

	public CollectionEdit(ActionHandler<T> handler, Collection<T> collectionToModify) {
		this.handler = handler;
		this.changingCollection = collectionToModify;
		this.itemList = new List();
		this.searchField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			handler.goBack();
		}), 0.025f, 0.7f, 0.175f, 0.8f);
		
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL), 0.025f, 0.6f, 0.15f, 0.7f);
		addComponent(searchField, 0.025f, 0.5f, 0.28f, 0.6f);
		
		addComponent(itemList, 0.3f, 0f, 1f, 0.9f);
		
		// This one is a bit of a hack, but works very well
		addComponent(new SimpleColorComponent(EditProps.BACKGROUND), 0.3f, 0.9f, 1f, 1f);
		
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
	}
	
	@Override
	public void keyPressed(int key) {
		String prev = searchField.getText();
		super.keyPressed(key);
		String next = searchField.getText();
		if (!prev.equals(next)) {
			itemList.refresh();
		}
	}
	
	@Override
	public void keyPressed(char key) {
		String prev = searchField.getText();
		super.keyPressed(key);
		String next = searchField.getText();
		if (!prev.equals(next)) {
			itemList.refresh();
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void init() {
		if(didInit) itemList.refresh();
		super.init();
	}
	
	private class List extends GuiMenu {

		@Override
		protected void addComponents() {
			
			String filter = searchField.getText().toLowerCase(Locale.ROOT);
			
			boolean hasImage = false;
			for (T item : changingCollection) {
				if (handler.getLabel(item).toLowerCase(Locale.ROOT).contains(filter) 
						&& handler.getImage(item) != null) {
					hasImage = true;
					break;
				}
			}
			
			int index = 0;
			for (T item : changingCollection) {
				if (handler.getLabel(item).toLowerCase(Locale.ROOT).contains(filter)) {
					float minY = 0.9f - index * 0.11f;
					float maxY = 1f - index * 0.11f;
					String label = handler.getLabel(item);
					BufferedImage image = handler.getImage(item);
					float minTextX, maxTextX;
					if (hasImage)
						minTextX = 0.175f;
					else
						minTextX = 0.025f;
					if (image != null)
						addComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)), 0f, minY, 0.15f, maxY);
					maxTextX = min(0.5f, minTextX + 0.03f * label.length());
					addComponent(new DynamicTextComponent(label, EditProps.LABEL), minTextX, minY, maxTextX, maxY);
					addComponent(new DynamicTextButton("Edit", EditProps.BUTTON, EditProps.HOVER, () -> {
						state.getWindow().setMainComponent(handler.createEditMenu(item, CollectionEdit.this));
					}), 0.51f, minY, 0.62f, maxY);
					addComponent(new DynamicTextButton("Copy", EditProps.BUTTON, EditProps.HOVER, () -> {
						state.getWindow().setMainComponent(handler.createCopyMenu(item, CollectionEdit.this));
					}), 0.64f, minY, 0.76f, maxY);
					addComponent(new DynamicTextButton("Delete", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
						String error = handler.deleteItem(item);
						if (error == null) {
							refresh();
						} else {
							errorComponent.setText(error);
						}
					}), 0.775f, minY, 0.975f, maxY);
					index++;
				}
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
		
		private void refresh() {
			clearComponents();
			addComponents();
		}
	}
	
	public static interface ActionHandler<T> {
		
		void goBack();
		
		BufferedImage getImage(T item);
		
		String getLabel(T item);
		
		GuiComponent createEditMenu(T itemToEdit, GuiComponent returnMenu);
		
		GuiComponent createCopyMenu(T itemToCopy, GuiComponent returnMenu);
		
		String deleteItem(T itemToDelete);
	}
}
