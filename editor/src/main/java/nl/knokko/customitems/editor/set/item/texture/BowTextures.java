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
package nl.knokko.customitems.editor.set.item.texture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class BowTextures extends NamedImage {
	
	private List<Entry> entries;

	public BowTextures(String name, BufferedImage texture, Entry[] entries) {
		super(name, texture);
		this.entries = new ArrayList<Entry>(entries.length);
		for (Entry entry : entries) {
			this.entries.add(entry);
		}
	}
	
	public BowTextures(String name, BufferedImage texture) {
		super(name, texture);
		entries = new ArrayList<Entry>(3);
		entries.add(new Entry(null, 0));
		entries.add(new Entry(null, 0.65));
		entries.add(new Entry(null, 0.9));
	}
	
	public BowTextures(BitInput input, boolean expectCompressed) throws IOException {
		super(input, expectCompressed);
		int size = input.readInt();
		entries = new ArrayList<Entry>(size);
		for (int index = 0; index < size; index++) {
			double pull = input.readDouble();
			BufferedImage image;
			if (expectCompressed) {
				byte[] imageBytes = input.readByteArray();
				InputStream imageInput = new ByteArrayInputStream(imageBytes);
				image = ImageIO.read(imageInput);
			} else {
				int width = input.readInt();
				int height = input.readInt();
				image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				input.increaseCapacity(32 * width * height);
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						image.setRGB(x, y, input.readDirectInt());
					}
				}
			}
			entries.add(new Entry(image, pull));
		}
	}
	
	@Override
	public void save(BitOutput output, boolean shouldCompress) throws IOException {
		super.save(output, shouldCompress);
		
		output.addInt(entries.size());
		for (Entry entry : entries) {
			output.addDouble(entry.pull);
			
			if (shouldCompress) {
				ByteArrayOutputStream imageOutput = new ByteArrayOutputStream();
				ImageIO.write(entry.texture, "PNG", imageOutput);
				byte[] imageBytes = imageOutput.toByteArray();
				output.addByteArray(imageBytes);
			} else {
				output.ensureExtraCapacity(32 * (2 + entry.texture.getWidth() * entry.getTexture().getHeight()));
				output.addDirectInt(entry.texture.getWidth());
				output.addDirectInt(entry.texture.getHeight());
				for (int x = 0; x < entry.texture.getWidth(); x++) {
					for (int y = 0; y < entry.texture.getHeight(); y++) {
						output.addDirectInt(entry.texture.getRGB(x, y));
					}
				}
			}
		}
	}
	
	public List<Entry> getPullTextures(){
		return entries;
	}
	
	public void setEntries(List<Entry> newEntries) {
		entries = newEntries;
	}
	
	public static class Entry {
		
		private BufferedImage texture;
		private double pull;
		
		public Entry(BufferedImage texture, double pull) {
			this.texture = texture;
			this.pull = pull;
		}
		
		@Override
		public Entry clone() {
			return new Entry(texture, pull);
		}
		
		public BufferedImage getTexture() {
			return texture;
		}
		
		public double getPull() {
			return pull;
		}
		
		public void setTexture(BufferedImage newTexture) {
			texture = newTexture;
		}
		
		public void setPull(double newPull) {
			pull = newPull;
		}
	}
}