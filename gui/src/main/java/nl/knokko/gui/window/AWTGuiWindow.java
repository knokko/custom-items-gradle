package nl.knokko.gui.window;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.AWTComponentState;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.keycode.AWTConverter;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.mousecode.AWTMouseConverter;
import nl.knokko.gui.render.AWTGuiRenderer;
import nl.knokko.gui.texture.loader.AWTTextureLoader;
import nl.knokko.gui.texture.loader.GuiTextureLoader;
import nl.knokko.gui.util.CharBuilder;
import nl.knokko.gui.window.input.CharacterFilter;

public class AWTGuiWindow extends GuiWindow {
	
	public static float getMouseX(JFrame frame, int x){
		Insets insets = frame.getInsets();
		return (float) (x - insets.left) / (frame.getWidth() - 1 - insets.right - insets.left);
	}
	
	public static float getMouseY(JFrame frame, int y){
		Insets insets = frame.getInsets();
		return 1 - (float) (y - insets.top) / (frame.getHeight() - 1 - insets.top - insets.bottom);
	}
	
	private JFrame frame;
	
	private final AWTTextureLoader textureLoader;
	private final AWTGuiRenderer guiRenderer;
	private final CharBuilder charBuilder;
	
	private int prevMouseX;
	private int prevMouseY;
	
	public AWTGuiWindow(){
		textureLoader = new AWTTextureLoader();
		guiRenderer = new AWTGuiRenderer(this);
		charBuilder = new CharBuilder(textureLoader);
	}

	public JFrame getFrame(){
		return frame;
	}
	
	@Override
	protected void preUpdate() {}
	
	@Override
	protected void postUpdate(){
		Point mouse = frame.getMousePosition();
		if (mouse != null) {
			if (prevMouseX != mouse.x || prevMouseY != mouse.y) {
				markChange();
			}
			prevMouseX = mouse.x;
			prevMouseY = mouse.y;
		} else {
			if (prevMouseX != -1 || prevMouseY != -1) {
				markChange();
			}
			prevMouseX = -1;
			prevMouseY = -1;
		}
	}
	
	private void invokeLater(Runnable action) {
		if (Thread.currentThread().getName().contains("AWT-EventQueue-")) {
			action.run();
		} else {
			SwingUtilities.invokeLater(action);
		}
	}
	
	@Override
	protected void directRender(){
		invokeLater(() -> {
			mainComponent.render(guiRenderer);
			guiRenderer.maybeRenderNow();
		});
	}
	
	@Override
	public void update() {
		invokeLater(super::update);
	}
	
	@Override
	public void setMainComponent(GuiComponent component) {
		invokeLater(() -> {
			super.setMainComponent(component);
		});
	}
	
	@Override
	protected void directClose(){
		invokeLater(() -> {
			frame.dispose();
		});
	}
	
	@Override
	protected void setMainComponentState() {
		invokeLater(super::setMainComponentState);
	}
	
	@Override
	protected void directOpen(String title, int width, int height, boolean border, BufferedImage iconImage) {
		invokeLater(() -> {
			frame = new JFrame();
			frame.add(guiRenderer.createPanel());
			frame.setUndecorated(!border);
			if (iconImage != null) {
				frame.setIconImage(iconImage);
			}
			frame.setSize(width, height);
			frame.setTitle(title);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setFocusTraversalKeysEnabled(false);
			Listener l = new Listener();
			frame.addKeyListener(l);
			frame.addMouseListener(l);
			frame.addMouseWheelListener(l);
			frame.addMouseMotionListener(l);
		});
	}
	
	@Override
	protected void directOpen(String title, boolean border, BufferedImage iconImage) {
		invokeLater(() -> {
			frame = new JFrame();
			frame.add(guiRenderer.createPanel());
			frame.setUndecorated(!border);
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			if (iconImage != null) {
				frame.setIconImage(iconImage);
			}
			
			/* 
			 * When a user starts moving or resizing the window, it will resize to the 'size' of the window,
			 * which is being set by this method. Notice that the 'size' will be ignored until the user starts
			 * moving or resizing the window (it will stay fullscreen until then). 
			 * 
			 * To make moving and resizing a bit more pleasant, the 'size' will be set to the screen size divided by 2.
			 */
			Rectangle windowBounds = frame.getGraphicsConfiguration().getBounds();
			frame.setSize(windowBounds.width / 2, windowBounds.height / 2);
			
			frame.setTitle(title);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setFocusTraversalKeysEnabled(false);
			Listener l = new Listener();
			frame.addKeyListener(l);
			frame.addMouseListener(l);
			frame.addMouseWheelListener(l);
			frame.addMouseMotionListener(l);
		});
	}
	
	@Override
	protected GuiComponentState createState(){
		return new AWTComponentState(this);
	}
	
	@Override
	public void run(int fps){
		int frameTime = 1000000000 / fps;
		try {
			
			// First wait until the main component and frame have been set
			try {
				SwingUtilities.invokeAndWait(() -> {});
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			
			// Then start running
			while(!shouldStopRunning && frame.isDisplayable()){
				long startTime = System.nanoTime();
				update();
				render();
				long sleepTime = startTime + frameTime - System.nanoTime();
				if(sleepTime > 0) {
					long millis = sleepTime / 1000000;
					Thread.sleep(millis, (int) (sleepTime - millis * 1000000));
				} //if sleepTime smaller than 0, we are behind schedule
			}
		} catch(InterruptedException ex){
			ex.printStackTrace();
		}
		close();
	}
	
	@Override
	public GuiTextureLoader getTextureLoader() {
		return textureLoader;
	}
	
	@Override
	public AWTGuiRenderer getRenderer() {
		return guiRenderer;
	}
	
	@Override
	public CharBuilder getCharBuilder() {
		return charBuilder;
	}
	
	@Override
	public float getMouseX() {
		Point mouse = frame.getMousePosition();
		if(mouse == null)
			return Float.NaN;
		Insets insets = frame.getInsets();
		return (float) (mouse.x - insets.left) / (frame.getWidth() - 1 - insets.right - insets.left);
	}
	
	@Override
	public float getMouseY() {
		Point mouse = frame.getMousePosition();
		if(mouse == null)
			return Float.NaN;
		Insets insets = frame.getInsets();
		return 1 - (float) (mouse.y - insets.top) / (frame.getHeight() - 1 - insets.top - insets.bottom);
	}
	
	@Override
	public float getMouseDX() {
		Point mouse = frame.getMousePosition();
		Insets insets = frame.getInsets();
		if (mouse != null && prevMouseX != -1)
			return (float) (mouse.x - prevMouseX) / (frame.getWidth() - insets.right - insets.left);
		return 0;
	}
	
	@Override
	public float getMouseDY() {
		Point mouse = frame.getMousePosition();
		Insets insets = frame.getInsets();
		if (mouse != null && prevMouseY != -1)
			return (float) -(mouse.y - prevMouseY) / (frame.getHeight() - insets.top - insets.bottom);
		return 0;
	}
	
	private class Listener implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {

		public void mouseDragged(MouseEvent e) {}

		public void mouseMoved(MouseEvent e) {}

		public void mouseWheelMoved(MouseWheelEvent event) {
			//well... I will need magic numbers sometimes...
			float amount = event.getUnitsToScroll() * -0.01f;
			if(amount != 0){
				mainComponent.scroll(amount);
			}
		}

		public void mouseClicked(MouseEvent event) {
			/*
			 * Because even moving the mouse a little between pressing and
			 * releasing causes Swing to consider it a drag event rather than a
			 * click event, we use mouseReleased to handle clicks instead.
			 */
		}

		public void mousePressed(MouseEvent event) {
			input.setMouseDown(AWTMouseConverter.getMouseButton(event.getButton()));
		}

		public void mouseReleased(MouseEvent event) {
			float x = getMouseX(frame, event.getX());
			float y = getMouseY(frame, event.getY());
			int button = AWTMouseConverter.getMouseButton(event.getButton());
			if (input.isMouseButtonDown(button)) {
				mainComponent.click(x, y, button);
				input.setMouseUp(AWTMouseConverter.getMouseButton(event.getButton()));
			}
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void keyTyped(KeyEvent event) {
			char c = event.getKeyChar();
			if(c != KeyEvent.CHAR_UNDEFINED && CharacterFilter.approve(c)){
				mainComponent.keyPressed(c);
			}
		}

		public void keyPressed(KeyEvent event) {
			int[] codes = AWTConverter.getDirect(event.getKeyCode());
			if (codes != null && codes[0] != KeyCode.UNDEFINED){
				for (int code : codes){
					mainComponent.keyPressed(code);
					input.setKeyDown(code);
				}
			}
		}

		public void keyReleased(KeyEvent event) {
			int[] codes = AWTConverter.getDirect(event.getKeyCode());
			if(codes != null && codes[0] != KeyCode.UNDEFINED){
				for(int code : codes){
					mainComponent.keyReleased(code);
					input.setKeyUp(code);
				}
			}
		}
	}

	@Override
	public int getPosX() {
		if (isOpen()) {
			return frame.getX() + frame.getInsets().left;
		} else {
			return -1;
		}
	}

	@Override
	public int getWidth() {
		if (isOpen()) {
			Insets insets = frame.getInsets();
			return frame.getWidth() - insets.left - insets.right;
		} else {
			return -1;
		}
	}

	@Override
	public int getPosY() {
		if (isOpen()) {
			return frame.getY() + frame.getInsets().top;
		} else {
			return -1;
		}
	}

	@Override
	public int getHeight() {
		if (isOpen()) {
			Insets insets = frame.getInsets();
			return frame.getHeight() - insets.top - insets.bottom;
		} else {
			return -1;
		}
	}
}
