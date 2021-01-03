package nl.knokko.customitems.editor.menu.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

public abstract class QuickCollectionEdit<T> extends GuiMenu {
	
	protected final List<T> ownCollection;
	
	protected final Consumer<Collection<T>> onApply;
	protected final GuiComponent returnMenu;
	
	protected final DynamicTextComponent errorComponent;
	
	protected GuiTexture deleteBase;
	protected GuiTexture deleteHover;

	public QuickCollectionEdit(Collection<T> currentCollection, 
			Consumer<Collection<T>> onApply, GuiComponent returnMenu) {
		
		this.ownCollection = new ArrayList<>(currentCollection);
		
		this.onApply = onApply;
		this.returnMenu = returnMenu;
		
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		
		GuiTextureLoader loader = state.getWindow().getTextureLoader();
		deleteBase = loader.loadTexture("nl/knokko/gui/images/icons/delete.png");
		deleteHover = loader.loadTexture("nl/knokko/gui/images/icons/delete_hover.png");
		
		addItemComponents();
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.175f, 0.9f);
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			onApply.accept(ownCollection);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.1f, 0.175f, 0.2f);
		
		addComponent(new DynamicTextButton("Add new", EditProps.BUTTON, EditProps.HOVER, () -> {
			ownCollection.add(addNew());
			refresh();
		}), 0.025f, 0.6f, 0.2f, 0.7f);
		
		HelpButtons.addHelpLink(this, getHelpPage());
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	protected void refresh() {
		clearComponents();
		addComponents();
	}
	
	protected void addItemComponents() {
		
		for (int index = 0; index < ownCollection.size(); index++) {
			
			float maxY = 0.9f - index * 0.1f;
			float minY = 0.8f - index * 0.1f;
			
			addRowComponents(index, minY, maxY);
		}
	}
	
	protected void removeItem(int indexToRemove) {
		ownCollection.remove(indexToRemove);
		refresh();
	}
	
	protected abstract void addRowComponents(int itemIndex, float minY, float maxY);
	
	protected abstract T addNew();
	
	protected abstract String getHelpPage();
}
