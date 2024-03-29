package nl.knokko.gui.component.menu;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder.Properties;

public class TextListEditMenu extends GuiMenu {
	
	public static final BufferedImage ADD_IMAGE;
	public static final BufferedImage ADD_HOVER_IMAGE;
	public static final BufferedImage DELETE_IMAGE;
	public static final BufferedImage DELETE_HOVER_IMAGE;
	
	static {
		try {
			ADD_IMAGE = ImageIO.read(TextListEditMenu.class.getClassLoader().getResource("nl/knokko/gui/images/icons/add.png"));
			ADD_HOVER_IMAGE = ImageIO.read(TextListEditMenu.class.getClassLoader().getResource("nl/knokko/gui/images/icons/add_hover.png"));
			DELETE_IMAGE = ImageIO.read(TextListEditMenu.class.getClassLoader().getResource("nl/knokko/gui/images/icons/delete.png"));
			DELETE_HOVER_IMAGE = ImageIO.read(TextListEditMenu.class.getClassLoader().getResource("nl/knokko/gui/images/icons/delete_hover.png"));
		} catch (IOException | IllegalArgumentException e) {
			throw new Error("Can't load required images for the Gui library: " + e.getMessage());
		}
	}
	
	protected Properties cancelProperties;
	protected Properties cancelHoverProperties;
	protected Properties applyProperties;
	protected Properties applyHoverProperties;
	protected Properties textProperties;
	protected Properties textActiveProperties;
	
	protected GuiTexture addTexture;
	protected GuiTexture addHoverTexture;
	protected GuiTexture deleteTexture;
	protected GuiTexture deleteHoverTexture;
	
	protected GuiColor backgroundColor;
	
	protected List<SubComponent> edits;
	
	protected List<String> initialStrings;
	
	protected GuiComponent previousMenu;
	protected Consumer<List<String>> returnAction;

	public TextListEditMenu(GuiComponent previousMenu, Consumer<List<String>> returnAction, GuiColor backgroundColor,
							Properties cancelProperties, Properties cancelHoverProperties,
							Properties applyProperties, Properties applyHoverProperties,
							Properties textProperties, Properties textActiveProperties, List<String> initialStrings) {
		this.initialStrings = initialStrings;
		this.edits = new ArrayList<>(initialStrings.size());
		this.previousMenu = previousMenu;
		this.returnAction = returnAction;
		
		this.backgroundColor = backgroundColor;
		this.cancelProperties = cancelProperties;
		this.cancelHoverProperties = cancelHoverProperties;
		this.applyProperties = applyProperties;
		this.applyHoverProperties = applyHoverProperties;
		this.textProperties = textProperties;
		this.textActiveProperties = textActiveProperties;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	protected void addComponents() {
		addComponent(new TextButton("Cancel", cancelProperties, cancelHoverProperties, () -> {
			state.getWindow().setMainComponent(previousMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new TextButton("Add line", applyProperties, applyHoverProperties, () -> {
			addLine(edits.size(), "Text...");
			state.getWindow().markChange();
		}), 0.1f, 0.5f, 0.25f, 0.6f);
		addComponent(new TextButton("Apply", applyProperties, applyHoverProperties, () -> {
			List<String> result = new ArrayList<>(edits.size());
			for(SubComponent component : edits) {
				result.add(((TextEditField) component.getComponent()).getText());
			}
			returnAction.accept(result);
			state.getWindow().setMainComponent(previousMenu);
		}), 0.1f, 0.3f, 0.25f, 0.4f);
		addTexture = state.getWindow().getTextureLoader().loadTexture(ADD_IMAGE);
		addHoverTexture = state.getWindow().getTextureLoader().loadTexture(ADD_HOVER_IMAGE);
		deleteTexture = state.getWindow().getTextureLoader().loadTexture(DELETE_IMAGE);
		deleteHoverTexture = state.getWindow().getTextureLoader().loadTexture(DELETE_HOVER_IMAGE);
		for (int index = 0; index < initialStrings.size(); index++) {
			addLine(index, initialStrings.get(index));
		}
	}
	
	protected void addLine(final int i, String line) {
		float minY = 0.9f - edits.size() * 0.15f;
		float maxY = 1f - i * 0.15f;
		addComponent(new TextEditField(line, textProperties, textActiveProperties), 0.35f, minY, 0.75f, maxY);
		addComponent(new ImageButton(addTexture, addHoverTexture, () -> {
			addLine(edits.size(), ((TextComponent) edits.get(edits.size() - 1).getComponent()).getText());
			for(int j = edits.size() - 2; j >= i; j--) {
				edits.get(j + 1).setComponent(edits.get(j).getComponent());
			}
			edits.get(i).setComponent(new TextEditField("", textProperties, textActiveProperties));
		}), 0.75f, minY, 0.85f, maxY);
		addComponent(new ImageButton(deleteTexture, deleteHoverTexture, () -> {
			for(int j = i + 1; j < edits.size(); j++) {
				edits.get(j - 1).setComponent(edits.get(j).getComponent());
			}
			SubComponent rm1 = getComponents().get(getComponents().size() - 3);
			SubComponent rm2 = getComponents().get(getComponents().size() - 2);
			SubComponent rm3 = getComponents().get(getComponents().size() - 1);
			removeComponent(rm1);
			removeComponent(rm2);
			removeComponent(rm3);
		}), 0.875f, minY, 0.975f, maxY);
	}
	
	@Override
	public void addComponent(SubComponent subComponent) {
		if(subComponent.getComponent() instanceof TextEditField)
			edits.add(subComponent);
		super.addComponent(subComponent);
	}
	
	@Override
	public void removeComponent(SubComponent subComponent) {
		if(subComponent.getComponent() instanceof TextEditField)
			edits.remove(subComponent);
		super.removeComponent(subComponent);
	}
}
