package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.menu.edit.block.BlockCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.container.ContainerPortal;
import nl.knokko.customitems.editor.menu.edit.drops.DropsMenu;
import nl.knokko.customitems.editor.menu.edit.export.ExportMenu;
import nl.knokko.customitems.editor.menu.edit.item.ItemCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.misc.CombinedResourcepackCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.projectile.ProjectileMenu;
import nl.knokko.customitems.editor.menu.edit.recipe.RecipeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.sound.SoundTypeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.texture.TextureCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.worldgen.WorldGenerationPortal;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.wiki.WikiGenerator;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.io.File;
import java.io.IOException;

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
		state.getWindow().setWindowCloseComponent(oldComponent -> new ConfirmQuitMenu(
				this, set, fileName
		));
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
		addComponent(this.errorComponent, 0.255F, 0.9F, 0.995F, 1.0F);
		addComponent(new DynamicTextButton("Quit", QUIT_BASE, QUIT_HOVER, () -> {
				this.state.getWindow().setWindowCloseComponent(null);
				this.state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.05F, 0.88F, 0.25F, 0.98F);
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
				this.state.getWindow().setWindowCloseComponent(null);
				this.state.getWindow().setMainComponent(MainMenu.INSTANCE);
			} catch (IOException io) {
				setError(io.getLocalizedMessage());
			}
		}), 0.1F, 0.59F, 0.35F, 0.69F);
		addComponent(new DynamicTextButton("Export...", SAVE_BASE, SAVE_HOVER, () -> {
			state.getWindow().setMainComponent(new ExportMenu(set, this, fileName));
		}), 0.1f, 0.48f, 0.3f, 0.58f);

		addComponent(new DynamicTextButton("Generate wiki", BUTTON, HOVER, () -> {
			try {
				new WikiGenerator(set, fileName).generate(new File(EditorFileManager.FOLDER + "/wiki/" + fileName));
				state.getWindow().setMainComponent(new AfterWikiMenu(fileName, this));
			} catch (IOException ioTrouble) {
				errorComponent.setText("Failed to create wiki: " + ioTrouble.getMessage());
			}
		}), 0.025f, 0.05f, 0.2f, 0.13f);

		addComponent(new DynamicTextButton("Textures", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.textureOverview);
		}), 0.6F, 0.825F, 0.8F, 0.9F);
		addComponent(new DynamicTextButton("Items", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.itemOverview);
		}), 0.6F, 0.735F, 0.8F, 0.81F);
		addComponent(new DynamicTextButton("Recipes", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.recipeOverview);
		}), 0.6F, 0.645F, 0.8F, 0.72F);
		addComponent(new DynamicTextButton("Drops", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.dropsMenu);
		}), 0.6F, 0.555F, 0.8F, 0.63F);
		addComponent(new DynamicTextButton("Projectiles", BUTTON, HOVER, () -> {
			this.state.getWindow().setMainComponent(this.projectileMenu);
		}), 0.6F, 0.465F, 0.875F, 0.54F);
		addComponent(new DynamicTextButton("Containers", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(containerPortal);
		}), 0.6f, 0.375f, 0.875f, 0.45f);
		addComponent(new DynamicTextButton("Blocks (1.13+)", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new BlockCollectionEdit(this, false));
		}), 0.6f, 0.285f, 0.875f, 0.36f);
		addComponent(new DynamicTextButton("Sounds", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new SoundTypeCollectionEdit(this, set));
		}), 0.6f, 0.195f, 0.8f, 0.27f);
		addComponent(new DynamicTextButton("World generation", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new WorldGenerationPortal(this, set));
		}), 0.6f, 0.105f, 0.9f, 0.18f);
		addComponent(new DynamicTextButton("Combined resourcepacks", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CombinedResourcepackCollectionEdit(this, set));
		}), 0.6f, 0.015f, 0.95f, 0.09f);

		HelpButtons.addHelpLink(this, "edit menu/index.html");
	}
}