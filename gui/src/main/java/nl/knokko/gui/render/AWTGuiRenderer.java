package nl.knokko.gui.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.window.AWTGuiWindow;

import nl.knokko.gui.render.RenderCommand;

public class AWTGuiRenderer extends GuiRenderer {
	
	private Graphics g;
	private final AWTGuiWindow window;
	
	private List<RenderCommand> renderCommands;
	
	private int minX;
	private int minY;
	//private int maxX; this field is never used
	private int maxY;
	private int deltaX;
	private int deltaY;
	
	public AWTGuiRenderer(AWTGuiWindow window) {
		this.window = window;
	}
	
	public JPanel createPanel() {
		return new AWTPanel();
	}
	
	public void set(Graphics graphics, int minX, int minY, int maxX, int maxY){
		this.g = graphics;
		this.minX = minX;
		this.minY = minY;
		//this.maxX = maxX;
		this.maxY = maxY;
		deltaX = maxX - minX;
		deltaY = maxY - minY;
	}
	
	public void setGraphics(Graphics newGraphics){
		g = newGraphics;
	}

	@Override
	public GuiRenderer getArea(float minX, float minY, float maxX, float maxY) {
		return new RelativeGuiRenderer.Static(this, minX, minY, maxX, maxY);
	}

	@Override
	void renderTextureNow(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
		g.drawImage(texture.getImage(), this.minX + Math.round(deltaX * minX), this.maxY - Math.round(deltaY * maxY), this.minX + Math.round(deltaX * maxX), this.maxY - Math.round(deltaY * minY), texture.getMinX(), texture.getMinY(), texture.getMaxX(), texture.getMaxY(), null);
	}

	@Override
	void fillNow(GuiColor color, float minX, float minY, float maxX, float maxY) {
		g.setColor(new Color(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF()));
		int minScreenX = this.minX + (int) (deltaX * minX);
		int minScreenY = this.maxY - (int) Math.ceil(deltaY * maxY);
		g.fillRect(minScreenX, minScreenY, (int) Math.ceil(deltaX * maxX) - minScreenX + 1, (int) Math.ceil(deltaY * (1 - minY)) - minScreenY + 1);
	}

	@Override
	void clearNow(GuiColor color) {
		g.setColor(new Color(color.getRedF(), color.getGreenF(), color.getBlueF()));
		g.fillRect(minX, minY, deltaX, deltaY);
	}
	
	@Override
	void renderNow(List<RenderCommand> renderCommands) {
		this.renderCommands = renderCommands;
		window.getFrame().repaint();
	}
	
	private class AWTPanel extends JPanel {
		
		private static final long serialVersionUID = -8159365387381684172L;

		@Override
		public void paint(Graphics g) {
			JFrame frame = window.getFrame();
			Insets insets = frame.getInsets();
			set(g, 0, 0, frame.getWidth() - 1 - insets.right - insets.left, frame.getHeight() - 1 - insets.bottom - insets.top);
			if (renderCommands != null) {
				AWTGuiRenderer.super.renderNow(renderCommands);
			} else {
				window.getMainComponent().render(AWTGuiRenderer.this);
				maybeRenderNow();
			}
		}
	}
}