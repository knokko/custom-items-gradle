/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class SelectTexture extends GuiMenu {

	private final GuiComponent returnMenu;
	private final Consumer<NamedImage> returnAction;
	private final TextureFilter filter;
	private final CreateMenuFactory createMenuFactory;
	private final ItemSet set;
	
	private final TextureList textureList;
	private final TextEditField searchField;

	public SelectTexture(ItemSet set, GuiComponent returnMenu, TextureFilter filter, 
			CreateMenuFactory createMenuFactory, Consumer<NamedImage> returnAction) {
		this.returnMenu = returnMenu;
		this.returnAction = returnAction;
		this.filter = filter;
		this.createMenuFactory = createMenuFactory;
		this.set = set;
		
		this.textureList = new TextureList();
		this.searchField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void init() {
		if(didInit) textureList.refresh();
		super.init();
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.6f, 0.3f, 0.7f);
		addComponent(new DynamicTextButton("Load texture...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(createMenuFactory.getCreateMenu(set, returnMenu, returnAction));
		}), 0.1f, 0.4f, 0.35f, 0.5f);
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL), 0.1f, 0.2f, 0.2f, 0.3f);
		addComponent(searchField, 0.1f, 0.1f, 0.3f, 0.2f);
		searchField.setFocus();
		addComponent(textureList, 0.4f, 0.1f, 0.85f, 0.9f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/texture.html");
	}
	
	@Override
	public void keyPressed(int key) {
		String prev = searchField.getText();
		super.keyPressed(key);
		String next = searchField.getText();
		if (!prev.equals(next)) {
			textureList.refresh();
		}
	}
	
	@Override
	public void keyPressed(char key) {
		String prev = searchField.getText();
		super.keyPressed(key);
		String next = searchField.getText();
		if (!prev.equals(next)) {
			textureList.refresh();
		}
	}
	
	protected class TextureList extends GuiMenu {

		@Override
		protected void addComponents() {
			addTextureComponents();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
		
		protected void refresh() {
			clearComponents();
			addTextureComponents();
		}
		
		protected void addTextureComponents() {
			Collection<NamedImage> textures = set.getBackingTextures();
			int index = 0;
			for (NamedImage texture : textures) {
				if (filter.approve(texture) && texture.getName().toLowerCase(Locale.ROOT).contains(searchField.getText().toLowerCase(Locale.ROOT))) {
					addComponent(
							new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture.getImage())),
							0f, 0.9f - index * 0.15f, 0.25f, 1f - index * 0.15f);
					addComponent(new DynamicTextButton(texture.getName(), EditProps.SELECT_BASE, EditProps.SELECT_HOVER, () -> {
						returnAction.accept(texture);
						state.getWindow().setMainComponent(returnMenu);
					}), 0.25f, 0.9f - index * 0.15f, 1f, 1f - index * 0.15f);
					index++;
				}
			}
		}
	}

	public static interface TextureFilter {

		boolean approve(NamedImage texture);
	}
	
	@FunctionalInterface
	public static interface CreateMenuFactory {
		
		GuiComponent getCreateMenu(ItemSet set, GuiComponent returnMenu, Consumer<NamedImage> afterSave);
	}
}