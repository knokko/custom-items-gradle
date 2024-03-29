package nl.knokko.customitems.editor.menu.edit;

import java.awt.Color;
import java.awt.Font;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.util.TextBuilder;
import nl.knokko.gui.util.TextBuilder.Properties;

public class EditProps {
	
	public static final Font FONT = new Font("Open Sans, Lucida Sans", Font.BOLD, 40);
	
	public static final Properties ERROR = Properties.createLabel(Color.RED, 2048, 256);
	
	public static final Properties SAVE_BASE = Properties.createButton(FONT, new Color(170, 200, 170), new Color(40, 50, 40), Color.BLACK);
	public static final Properties SAVE_HOVER = Properties.createButton(FONT, new Color(210, 255, 210), new Color(50, 65, 50), Color.BLACK);
	
	public static final Properties QUIT_BASE = Properties.createButton(FONT, new Color(200, 170, 170), new Color(50, 40, 40), Color.BLACK);
	public static final Properties QUIT_HOVER = Properties.createButton(FONT, new Color(250, 210, 210), new Color(65, 50, 50), Color.BLACK);
	
	public static final Properties CANCEL_BASE = Properties.createButton(FONT, new Color(200, 200, 170), new Color(50, 50, 40), Color.BLACK);
	public static final Properties CANCEL_HOVER = Properties.createButton(FONT, new Color(255, 255, 210), new Color(65, 65, 50), Color.BLACK);
	
	public static final Properties BUTTON = Properties.createButton(FONT, new Color(200, 200, 200), new Color(50, 50, 50), Color.BLACK);
	public static final Properties HOVER = Properties.createButton(FONT, new Color(255, 255, 255), new Color(65, 65, 65), Color.BLACK);
	
	public static final Properties LABEL = Properties.createLabel();
	public static final Properties LINK_BASE = Properties.createLabel(new Color(0, 0, 100));
	public static final Properties LINK_HOVER = Properties.createLabel(new Color(0, 0, 200));
	
	public static final Properties EDIT_BASE = Properties.createEdit(new Color(200, 200, 200), new Color(50, 50, 50));
	public static final Properties EDIT_ACTIVE = Properties.createEdit(new Color(255, 255, 255), new Color(65, 65, 65));

	public static final Properties LONG_EDIT_BASE = new Properties(
			FONT, Color.BLACK, new Color(200, 200, 200), new Color(50, 50, 50),
			TextBuilder.HorAlignment.LEFT, TextBuilder.VerAlignment.MIDDLE,
			0.005f, 0.05f, 0.01f, 0.1f, -1, -1
	);

	public static final Properties LONG_EDIT_ACTIVE = new Properties(
			FONT, Color.BLACK, new Color(255, 255, 255), new Color(65, 65, 65),
			TextBuilder.HorAlignment.LEFT, TextBuilder.VerAlignment.MIDDLE,
			0.005f, 0.05f, 0.01f, 0.1f, -1, -1
	);
	
	public static final Properties SELECT_BASE = Properties.createText(FONT, Color.BLACK, new Color(170, 200, 200), 512, 128);
	public static final Properties SELECT_HOVER = Properties.createText(FONT, new Color(50, 50, 50), new Color(210, 250, 250), 512, 128);
	public static final Properties SELECT_ACTIVE = Properties.createText(FONT, new Color(70, 70, 70), new Color(210, 255, 255), 512, 128);
	
	public static final Properties CHOOSE_BASE = Properties.createButton(FONT, new Color(200, 170, 200), new Color(50, 40, 50), Color.BLACK);
	public static final Properties CHOOSE_HOVER = Properties.createButton(FONT, new Color(255, 210, 255), new Color(65, 50, 65), Color.BLACK);
	
	public static final GuiColor BACKGROUND = new SimpleGuiColor(170, 170, 200);
	public static final GuiColor BACKGROUND2 = new SimpleGuiColor(200, 170, 170);
}