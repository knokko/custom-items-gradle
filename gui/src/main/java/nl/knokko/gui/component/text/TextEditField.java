/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
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
package nl.knokko.gui.component.text;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.mousecode.MouseCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.testing.EditableComponent;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;
import nl.knokko.gui.util.TextBuilder.Properties;

public class TextEditField extends TextComponent implements EditableComponent {
	
	protected GuiTexture activeTexture;
	protected Properties activeProperties;
	
	protected Point2D.Float tabSwitchPoint;
	
	protected boolean active;

	public TextEditField(String text, Properties passiveProperties, Properties activeProperties) {
		super(text, passiveProperties);
		this.activeProperties = activeProperties;
	}
	
	@Override
	public void update() {
		super.update();
		if (tabSwitchPoint != null) {
			state.getWindow().getMainComponent().click(
					tabSwitchPoint.x, tabSwitchPoint.y, MouseCode.BUTTON_LEFT
			);
			tabSwitchPoint = null;
		}
	}
	
	@Override
	public void render(GuiRenderer renderer){
		if(active)
			renderer.renderTexture(activeTexture, 0, 0, 1, 1);
		else
			super.render(renderer);
	}
	
	@Override
	protected void updateTexture(){
		updatePassiveTexture();
		updateActiveTexture();
	}
	
	public void setActiveProperties(Properties newProperties){
		activeProperties = newProperties;
		updateActiveTexture();
	}
	
	protected void updatePassiveTexture(){
		texture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, properties));
		state.getWindow().markChange();
	}
	
	protected void updateActiveTexture(){
		activeTexture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, activeProperties));
		state.getWindow().markChange();
	}
	
	@Override
	public void click(float x, float y, int button){
		if(button == MouseCode.BUTTON_LEFT) {
			active = !active;
			state.getWindow().markChange();
		}
	}
	
	@Override
	public void clickOut(int button){
		active = false;
		state.getWindow().markChange();
	}
	
	@Override
	public void keyPressed(char character){
		if(active && !state.getWindow().getInput().isKeyDown(KeyCode.KEY_CONTROL)
				&& character != '\t'){
			text += character;
			updateTexture();
		}
	}
	
	@Override
	public void keyPressed(int key){
		if(key == KeyCode.KEY_ESCAPE || key == KeyCode.KEY_ENTER) {
			active = false;
			state.getWindow().markChange();
		}
		if(active){
			if (state.getWindow().getInput().isKeyDown(KeyCode.KEY_CONTROL)) {
				if (key == KeyCode.KEY_V) {
					String clipboardText = getClipboardText();
					paste(clipboardText);
				} else if (key == KeyCode.KEY_C && !text.isEmpty()) {
					setClipboardText(text);
				} else if (key == KeyCode.KEY_X) {
					setClipboardText(text);
					text = "";
					updateTexture();
				}
			} else if(key == KeyCode.KEY_BACKSPACE && text.length() > 0){
				text = text.substring(0, text.length() - 1);
				updateTexture();
			} else if(key == KeyCode.KEY_DELETE && text.length() > 0){
				text = text.substring(0);
				updateTexture();
			} else if (key == KeyCode.KEY_TAB) {
				GuiComponent mainComponent = state.getWindow().getMainComponent();
				if (mainComponent instanceof EditableComponent) {
					
					EditableComponent currentMenu = (EditableComponent) mainComponent;
					Collection<EditableComponent.Pair> pairs = currentMenu.getEditableLocations();
					EditableComponent.Pair bestPair = null;
					
					// Biggest midY is most important
					// Then biggest midX
					
					if (state.getWindow().getInput().isKeyDown(KeyCode.KEY_SHIFT)) {
						
						// Go to previous edit field
						for (EditableComponent.Pair pair : pairs) {
							if (pair.getLocation().y >= state.getMidY()) {
								
								boolean isCandidate = false;
								if (pair.getLocation().y == state.getMidY()) {
									isCandidate = pair.getLocation().x < state.getMidX();
								} else {
									isCandidate = true;
								}
								
								if (isCandidate) {
									if (bestPair == null) {
										bestPair = pair;
									} else {
										if (pair.getLocation().y < bestPair.getLocation().y) {
											bestPair = pair;
										} else if (pair.getLocation().y == bestPair.getLocation().y) {
											if (pair.getLocation().x > bestPair.getLocation().x) {
												bestPair = pair;
											}
										}
									}
								}
							}
						}
						
						if (bestPair != null) {
							tabSwitchPoint = bestPair.getLocation();
						}
					} else {
						
						// Go to next edit field
						for (EditableComponent.Pair pair : pairs) {
							if (pair.getLocation().y <= state.getMidY()) {
								
								boolean isCandidate = false;
								if (pair.getLocation().y == state.getMidY()) {
									isCandidate = pair.getLocation().x > state.getMidX();
								} else {
									isCandidate = true;
								}
								
								if (isCandidate) {
									if (bestPair == null) {
										bestPair = pair;
									} else {
										if (pair.getLocation().y > bestPair.getLocation().y) {
											bestPair = pair;
										} else if (pair.getLocation().y == bestPair.getLocation().y) {
											if (pair.getLocation().x < bestPair.getLocation().x) {
												bestPair = pair;
											}
										}
									}
								}
							}
						}
						
						if (bestPair != null) {
							tabSwitchPoint = bestPair.getLocation();
						}
					}
				}
			}
		}
	}
	
	protected void paste(String clipboardText) {
		if (clipboardText != null) {
			clipboardText = clipboardText.replace("\n", "").replace("\r", "");
			text += clipboardText;
		} else {
			text += "Can't paste";
		}
		updateTexture();
	}
	
	protected String getClipboardText() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (clipboard != null) {
			try {
				Transferable clipboardContent = clipboard.getContents(this);
				return (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
			} catch (IllegalStateException ex) {
				// See explanation of setClipboardText
				return null;
			} catch (UnsupportedFlavorException e) {
				// Ok, there is currently no text on the clipboard.
				// Let's just ignore it
				return null;
			} catch (IOException e) {
				// The requested clipboard data is no longer available, so we can't paste it
				return null;
			}
		} else {
			// See explanation of setClipboardText
			return null;
		}
	}
	
	protected void setClipboardText(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		/*
		 * It is possible that this method returns null, for instance when the
		 * operating system doesn't support a clipboard. If that is the case,
		 * we ignore it because there is nothing we can do about it.
		 */
		if (clipboard != null) {
			try {
				clipboard.setContents(new StringSelection(text), null);
			} catch (IllegalStateException ex) {
				/*
				 * This can occur if the clipboard is currently unavailable for
				 * some reason. When this occurs, we simply do nothing and the
				 * user can try to copy again.
				 */
			}
		}
	}
	
	public void setFocus() {
		if (!active) {
			active = true;
			state.getWindow().markChange();
		}
	}
	
	public void loseFocus() {
		if (active) {
			active = false;
			state.getWindow().markChange();
		}
	}

	@Override
	public Collection<EditableComponent.Pair> getEditableLocations() {
		Collection<EditableComponent.Pair> points = new ArrayList<>(1);
		points.add(new EditableComponent.Pair(this, new Float(state.getMidX(), state.getMidY())));
		return points;
	}
}