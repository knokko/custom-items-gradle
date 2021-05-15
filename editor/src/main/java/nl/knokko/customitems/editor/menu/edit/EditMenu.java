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
package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.customitems.editor.menu.edit.block.BlockCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.container.ContainerPortal;
import nl.knokko.customitems.editor.menu.edit.drops.DropsMenu;
import nl.knokko.customitems.editor.menu.edit.item.ItemCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.projectile.ProjectileMenu;
import nl.knokko.customitems.editor.menu.edit.recipe.RecipeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.texture.TextureCollectionEdit;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditMenu extends GuiMenu {

	protected final ItemSet set;

	protected final DynamicTextComponent errorComponent;

	protected final GuiComponent itemOverview;
	protected final GuiComponent textureOverview;
	protected final GuiComponent recipeOverview;
	protected final GuiComponent dropsMenu;
	protected final ProjectileMenu projectileMenu;
	protected final ContainerPortal containerPortal;

	public EditMenu(ItemSet set) {
		this.set = set;
		itemOverview = new ItemCollectionEdit(this);
		textureOverview = new TextureCollectionEdit(this);
		recipeOverview = new RecipeCollectionEdit(this);
		dropsMenu = new DropsMenu(this);
		projectileMenu = new ProjectileMenu(this);
		containerPortal = new ContainerPortal(this);
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	public ItemSet getSet() {
		return set;
	}

	public GuiComponent getItemOverview() {
		return itemOverview;
	}

	public GuiComponent getTextureOverview() {
		return textureOverview;
	}

	public GuiComponent getRecipeOverview() {
		return recipeOverview;
	}

	public GuiComponent getDropsMenu() {
		return dropsMenu;
	}

	public ProjectileMenu getProjectileMenu() {
		return projectileMenu;
	}

	public ContainerPortal getContainerPortal() {
		return containerPortal;
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	protected void setError(String error) {
		errorComponent.setProperties(EditProps.ERROR);
		errorComponent.setText(error);
	}

	protected void setInfo(String info) {
		errorComponent.setProperties(EditProps.LABEL);
		errorComponent.setText(info);
	}

	@Override
	protected void addComponents() {
		addComponent(this.errorComponent, 0.305F, 0.9F, 0.95F, 1.0F);
		addComponent(
				new DynamicTextButton("Quit", EditProps.QUIT_BASE, EditProps.QUIT_HOVER,
						() -> this.state.getWindow().setMainComponent(MainMenu.INSTANCE)),

				0.1F, 0.88F, 0.3F, 0.98F);
		addComponent(new DynamicTextButton("Save", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = this.set.save();
			if (error != null) {
				setError(error);
			} else {
				setInfo("Saved successfully");
			}

		}), 0.1F, 0.7F, 0.25F, 0.8F);
		addComponent(
				new DynamicTextButton("Save and quit", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
					String error = this.set.save();
					if (error != null) {
						setError(error);
					} else {
						this.state.getWindow().setMainComponent(MainMenu.INSTANCE);
					}
				}), 0.1F, 0.59F, 0.35F, 0.69F);
		addComponent(new DynamicTextButton("Export for 1.12", EditProps.SAVE_BASE, EditProps.SAVE_HOVER,
				() -> {
					String error = this.set.save();
					if (error != null) {
						setError(error);
					} else {
						error = this.set.exportFor12(12);
						if (error != null) {
							setError(error);
						} else {
							this.state.getWindow().setMainComponent(new AfterExportMenu(this));
						}
					}
				}), 0.1F, 0.48F, 0.35F, 0.58F);
		addComponent(new DynamicTextButton("Export for 1.13", EditProps.SAVE_BASE, EditProps.SAVE_HOVER,
				() -> {
					String error = this.set.save();
					if (error != null) {
						setError(error);
					} else {
						error = this.set.exportFor13OrLater(13);
						if (error != null) {
							setError(error);
						} else {
							this.state.getWindow().setMainComponent(new AfterExportMenu(this));
						}
					}
				}), 0.1F, 0.37F, 0.35F, 0.47F);
		addComponent(new DynamicTextButton("Export for 1.14", EditProps.SAVE_BASE, EditProps.SAVE_HOVER,
				() -> {
					String error = this.set.save();
					if (error != null) {
						setError(error);
					} else {
						error = this.set.exportFor13OrLater(14);
						if (error != null) {
							setError(error);
						} else {
							this.state.getWindow().setMainComponent(new AfterExportMenu(this));
						}
					}
				}), 0.1F, 0.26F, 0.35F, 0.36F);
		addComponent(new DynamicTextButton("Export for 1.15", EditProps.SAVE_BASE, EditProps.SAVE_HOVER,
				() -> {
					String error = this.set.save();
					if (error != null) {
						setError(error);
					} else {
						error = this.set.exportFor15();
						if (error != null) {
							setError(error);
						} else {
							this.state.getWindow().setMainComponent(new AfterExportMenu(this));
						}
					}
				}), 0.1F, 0.15F, 0.35F, 0.25F);
		addComponent(new DynamicTextButton("Export for 1.16", EditProps.SAVE_BASE, EditProps.SAVE_HOVER,
				() -> {
					String error = this.set.save();
					if (error != null) {
						setError(error);
					} else {
						error = this.set.exportFor16();
						if (error != null) {
							setError(error);
						} else {
							this.state.getWindow().setMainComponent(new AfterExportMenu(this));
						}
					}
				}), 0.1F, 0.04F, 0.35F, 0.14F);
		addComponent(
				new DynamicTextButton("Textures", EditProps.BUTTON, EditProps.HOVER,
						() -> this.state.getWindow().setMainComponent(this.textureOverview)),

				0.6F, 0.8F, 0.8F, 0.9F);
		addComponent(
				new DynamicTextButton("Items", EditProps.BUTTON, EditProps.HOVER,
						() -> this.state.getWindow().setMainComponent(this.itemOverview)),

				0.6F, 0.68F, 0.8F, 0.78F);
		addComponent(
				new DynamicTextButton("Recipes", EditProps.BUTTON, EditProps.HOVER,
						() -> this.state.getWindow().setMainComponent(this.recipeOverview)),

				0.6F, 0.56F, 0.8F, 0.66F);
		addComponent(
				new DynamicTextButton("Drops", EditProps.BUTTON, EditProps.HOVER,
						() -> this.state.getWindow().setMainComponent(this.dropsMenu)),

				0.6F, 0.44F, 0.8F, 0.54F);
		addComponent(
				new DynamicTextButton("Projectiles", EditProps.BUTTON, EditProps.HOVER,
						() -> this.state.getWindow().setMainComponent(this.projectileMenu)),

				0.6F, 0.32F, 0.875F, 0.42F);
		addComponent(new DynamicTextButton("Containers", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(containerPortal);
		}), 0.6f, 0.2f, 0.875f, 0.3f);
		addComponent(new DynamicTextButton("Blocks (1.13+)", EditProps.BUTTON, EditProps.HOVER, () ->
				state.getWindow().setMainComponent(new BlockCollectionEdit(set, this))
		), 0.6f, 0.08f, 0.9f, 0.18f);

		HelpButtons.addHelpLink(this, "edit%20menu/index.html");
	}
}