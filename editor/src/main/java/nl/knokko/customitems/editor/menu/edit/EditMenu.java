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

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.menu.edit.block.BlockCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.container.ContainerPortal;
import nl.knokko.customitems.editor.menu.edit.drops.DropsMenu;
import nl.knokko.customitems.editor.menu.edit.item.ItemCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.projectile.ProjectileMenu;
import nl.knokko.customitems.editor.menu.edit.recipe.RecipeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.texture.TextureCollectionEdit;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.io.IOException;

import static nl.knokko.customitems.MCVersions.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditMenu extends GuiMenu {

	protected final ItemSet set;
	protected final String fileName;

	protected final DynamicTextComponent errorComponent;

	protected final GuiComponent itemOverview;
	protected final GuiComponent textureOverview;
	protected final GuiComponent recipeOverview;
	protected final GuiComponent dropsMenu;
	protected final ProjectileMenu projectileMenu;
	protected final ContainerPortal containerPortal;

	public EditMenu(ItemSet set, String fileName) {
		this.set = set;
		this.fileName = fileName;
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

	private void saveAndExport(int mcVersion) {
		try {
			this.set.validateExportVersion(mcVersion);
			EditorFileManager.export(this.set, mcVersion, fileName);
			EditorFileManager.saveAndBackUp(this.set, fileName);
			state.getWindow().setMainComponent(new AfterExportMenu(this));
		} catch (IOException | ValidationException ex) {
			setError(ex.getLocalizedMessage());
		} catch (ProgrammingValidationException ex) {
			setError("Programming error: " + ex.getMessage());
		}
	}

	@Override
	protected void addComponents() {
		addComponent(this.errorComponent, 0.255F, 0.9F, 0.995F, 1.0F);
		addComponent(new DynamicTextButton("Quit", QUIT_BASE, QUIT_HOVER,
						() -> this.state.getWindow().setMainComponent(MainMenu.INSTANCE)
				), 0.05F, 0.88F, 0.25F, 0.98F);
		addComponent(new DynamicTextButton("Save", SAVE_BASE, SAVE_HOVER, () -> {
			try {
				EditorFileManager.saveAndBackUp(this.set, fileName);
				setInfo("Saved successfully");
			} catch (IOException io) {
				setError(io.getLocalizedMessage());
			}
		}), 0.1F, 0.7F, 0.25F, 0.8F);
		addComponent(new DynamicTextButton("Save and quit", SAVE_BASE, SAVE_HOVER, () -> {
			try {
				EditorFileManager.saveAndBackUp(this.set, fileName);
				this.state.getWindow().setMainComponent(MainMenu.INSTANCE);
			} catch (IOException io) {
				setError(io.getLocalizedMessage());
			}
		}), 0.1F, 0.59F, 0.35F, 0.69F);
		addComponent(new DynamicTextButton("Export for 1.12", SAVE_BASE, SAVE_HOVER, () -> {
			saveAndExport(VERSION1_12);
		}), 0.05F, 0.50F, 0.25F, 0.58F);
		addComponent(new DynamicTextButton("Export for 1.13", SAVE_BASE, SAVE_HOVER, () -> {
			saveAndExport(VERSION1_13);
		}), 0.05F, 0.41F, 0.25F, 0.49F);
		addComponent(new DynamicTextButton("Export for 1.14", SAVE_BASE, SAVE_HOVER, () -> {
			saveAndExport(VERSION1_14);
		}), 0.05F, 0.32F, 0.25F, 0.40F);
		addComponent(new DynamicTextButton("Export for 1.15", SAVE_BASE, SAVE_HOVER, () -> {
			saveAndExport(VERSION1_15);
		}), 0.05F, 0.23F, 0.25F, 0.31F);
		addComponent(new DynamicTextButton("Export for 1.16", SAVE_BASE, SAVE_HOVER, () -> {
			saveAndExport(VERSION1_16);
		}), 0.3F, 0.5F, 0.5F, 0.58F);
		addComponent(new DynamicTextButton("Export for 1.17", SAVE_BASE, SAVE_HOVER, () -> {
			saveAndExport(VERSION1_17);
		}), 0.3F, 0.41F, 0.5F, 0.49F);
		addComponent(new DynamicTextButton("Export for 1.18", SAVE_BASE, SAVE_HOVER, () -> {
			saveAndExport(VERSION1_18);
		}), 0.3F, 0.32F, 0.5F, 0.4F);
		addComponent(new DynamicTextButton("Export for 1.19", SAVE_BASE, SAVE_HOVER, () -> {
			saveAndExport(VERSION1_19);
		}), 0.3f, 0.23F, 0.5F, 0.31F);

		addComponent(new DynamicTextButton("Textures", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.textureOverview);
		}), 0.6F, 0.8F, 0.8F, 0.9F);
		addComponent(new DynamicTextButton("Items", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.itemOverview);
		}), 0.6F, 0.68F, 0.8F, 0.78F);
		addComponent(new DynamicTextButton("Recipes", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.recipeOverview);
		}), 0.6F, 0.56F, 0.8F, 0.66F);
		addComponent(new DynamicTextButton("Drops", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.dropsMenu);
		}), 0.6F, 0.44F, 0.8F, 0.54F);
		addComponent(new DynamicTextButton("Projectiles", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.projectileMenu);
		}), 0.6F, 0.32F, 0.875F, 0.42F);
		addComponent(new DynamicTextButton("Containers", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(containerPortal);
		}), 0.6f, 0.2f, 0.875f, 0.3f);
		addComponent(new DynamicTextButton("Blocks (1.13+)", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new BlockCollectionEdit(this));
		}), 0.6f, 0.08f, 0.9f, 0.18f);

		HelpButtons.addHelpLink(this, "edit menu/index.html");
	}
}