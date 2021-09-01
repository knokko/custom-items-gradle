package nl.knokko.customitems.editor.set.item.texture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ArmorTextures {
	
	private static final byte ENCODING_1 = 1;
	
	public static ArmorTextures load(BitInput input) throws UnknownEncodingException {
		ArmorTextures textures = new ArmorTextures();
		byte encoding = input.readByte();
		if (encoding == ENCODING_1) {
			textures.load1(input);
		} else {
			throw new UnknownEncodingException("ArmorTextures", encoding);
		}
		return textures;
	}
	
	private String name;
	
	private BufferedImage layer1;
	private BufferedImage layer2;
	
	private ArmorTextures() {}

	public ArmorTextures(
			String name, BufferedImage layer1, BufferedImage layer2
	) throws ValidationException {
		this.name = name;
		this.layer1 = layer1;
		this.layer2 = layer2;
		
		validate();
	}
	
	private void validate() throws ValidationException {
		// Catch programming errors
		Checks.notNull(name);
		
		// Catch validation errors
		if (name.isEmpty())
			throw new ValidationException("The name can't be empty");
		for (char character : name.toCharArray()) {
			// I am very strict with names because they will be used in file names
			// (I don't want to risk invalid file names)
			if (
					!(character >= '0' && character <= '9') &&
					!(character >= 'a' && character <= 'z') &&
					character != '_'
			) {
				throw new ValidationException(
						"The name must consist of only English letters, "
						+ "numbers, and underscores"
				);
			}
		}
		
		// For now, both layer1 and layer2 are required
		// This might change in the future
		validateTexture(layer1, "layer1");
		validateTexture(layer2, "layer2");
	}
	
	private void validateTexture(BufferedImage texture, String name) throws ValidationException {
		if (texture == null)
			throw new ValidationException("You need to pick a " + name + " texture");
		
		if (texture.getWidth() != 2 * texture.getHeight())
			throw new ValidationException("The width of " + name + " is not twice as big as the height");
		
		// It looks like optifine doesn't really have limitations on the texture size
	}

	public String getName() {
		return name;
	}
	
	public BufferedImage getLayer1() {
		return layer1;
	}
	
	public BufferedImage getLayer2() {
		return layer2;
	}
	
	public void save(BitOutput output) {
		save1(output);
	}
	
	private void save1(BitOutput output) {
		output.addByte(ENCODING_1);
		saveProps1(output);
	}
	
	private void saveProps1(BitOutput output) {
		output.addString(name);
		byte[] bytesOfLayer1;
		byte[] bytesOfLayer2;
		try {
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			ImageIO.write(layer1, "PNG", byteOutput);
			bytesOfLayer1 = byteOutput.toByteArray();
			byteOutput.reset();
			ImageIO.write(layer2, "PNG", byteOutput);
			bytesOfLayer2 = byteOutput.toByteArray();
		} catch (IOException shouldntHappen) {
			throw new RuntimeException("Computer is incapable of encoding images", shouldntHappen);
		}
		output.addByteArray(bytesOfLayer1);
		output.addByteArray(bytesOfLayer2);
	}
	
	private void load1(BitInput input) {
		loadProps1(input);
	}
	
	private void loadProps1(BitInput input) {
		name = input.readString();
		byte[] bytesOfLayer1 = input.readByteArray();
		byte[] bytesOfLayer2 = input.readByteArray();
		try {
			layer1 = ImageIO.read(new ByteArrayInputStream(bytesOfLayer1));
			layer2 = ImageIO.read(new ByteArrayInputStream(bytesOfLayer2));
		} catch (IOException shouldntHappen) {
			throw new IllegalArgumentException("Corrupted image input", shouldntHappen);
		}
	}
}
