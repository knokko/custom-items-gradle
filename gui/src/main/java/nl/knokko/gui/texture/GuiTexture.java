package nl.knokko.gui.texture;

import java.awt.Image;

public interface GuiTexture {
	
	int getTextureID();
	
	float getMinU();
	
	float getMinV();
	
	float getMaxU();
	
	float getMaxV();
	
	Image getImage();
	
	int getMinX();
	
	int getMinY();
	
	int getMaxX();
	
	int getMaxY();
	
	int getWidth();
	
	int getHeight();
}