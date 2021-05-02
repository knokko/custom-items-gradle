package nl.knokko.customitems.editor.set.projectile.cover;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SphereProjectileCover extends EditorProjectileCover {
	
	public NamedImage texture;
	
	public int slotsPerAxis;
	public double scale;
	
	public SphereProjectileCover(CustomItemType type, String name, 
			NamedImage texture, int slotsPerAxis, double scale) {
		super(type, name);
		this.texture = texture;
		this.slotsPerAxis = slotsPerAxis;
		this.scale = scale;
	}
	
	SphereProjectileCover(BitInput input, ItemSet set){
		super(input);
		this.texture = set.getTextureByName(input.readString());
		this.slotsPerAxis = input.readInt();
		this.scale = input.readDouble();
	}

	@Override
	protected byte getID() {
		return EditorProjectileCover.ID_SPHERE;
	}

	@Override
	protected void saveData(BitOutput output) {
		output.addString(texture.getName());
		output.addInt(slotsPerAxis);
		output.addDouble(scale);
	}

	@Override
	public void writeModel(ZipOutputStream output) throws IOException {
		String[] model = createBulletModel(texture.getName(), slotsPerAxis, scale);
		
		PrintWriter jsonWriter = new PrintWriter(output);
		for (String line : model) {
			jsonWriter.println(line);
		}
		jsonWriter.flush();
	}
	
	private static String[] createBulletModel(String textureName, int slotsPerAxis, double scale) {
		String[] start = {
				"{",
				"	\"textures\": {",
				"		\"t\": \"customitems/" + textureName + "\"",
				"	}, \"display\": {",
				"		\"ground\": {",
				"			\"rotation\": [0, 0, 0],",
				"			\"translation\": [0, -6.5, 0],",
				"			\"scale\": [" + scale + ", " + scale + ", " + scale + "]",
				"		}",
				"	}, \"elements\": ["
		};
		
		List<String> middle = new ArrayList<>(200);
		
		boolean[][][] slots = new boolean[slotsPerAxis][slotsPerAxis][slotsPerAxis];
		
		// Set all slots to true that are inside the sphere
		for (int x = 0; x < slotsPerAxis; x++) {
			double relX = (x + 0.5) / slotsPerAxis;
			double radX = (relX - 0.5) * 2.0;
			for (int y = 0; y < slotsPerAxis; y++) {
				double relY = (y + 0.5) / slotsPerAxis;
				double radY = (relY - 0.5) * 2.0;
				for (int z = 0; z < slotsPerAxis; z++) {
					double relZ = (z + 0.5) / slotsPerAxis;
					double radZ = (relZ - 0.5) * 2.0;
					
					double dist = radX * radX + radY * radY + radZ * radZ;
					if (dist <= 1.0001) {
						slots[x][y][z] = true;
					}
				}
			}
		}
		
		String[] faces = {"north", "east", "south", "west", "up", "down"};
		
		// Add all slots that are true in slots and will be visible
		for (int x = 0; x < slotsPerAxis; x++) {
			for (int y = 0; y < slotsPerAxis; y++) {
				for (int z = 0; z < slotsPerAxis; z++) {
					
					// Check that it is true in slots
					if (slots[x][y][z]) {
						
						// Check that it will be visible
						if (x == 0 || y == 0 || z == 0 || x == slotsPerAxis - 1 || y == slotsPerAxis - 1 
								|| z == slotsPerAxis - 1 || !slots[x + 1][y][z] || !slots[x - 1][y][z] 
								|| !slots[x][y + 1][z] || !slots[x][y - 1][z] || !slots[x][y][z + 1] 
								|| !slots[x][y][z - 1]) {
							
							// Now add a cube at the place of the slot
							double modelFromX = x * 16.0 / slotsPerAxis;
							double modelFromY = y * 16.0 / slotsPerAxis;
							double modelFromZ = z * 16.0 / slotsPerAxis;
							double modelToX = (x + 1) * 16.0 / slotsPerAxis;
							double modelToY = (y + 1) * 16.0 / slotsPerAxis;
							double modelToZ = (z + 1) * 16.0 / slotsPerAxis;
							
							double u = 8.0 + 8.0 * Math.atan2((modelFromZ + modelToZ) * 0.5 - 8.0, (modelFromX + modelToX) * 0.5 - 8.0) / Math.PI;
							double v = modelFromY;
							middle.add("		{");
							middle.add("			\"name\": \"Part(" + x + ", " + y + ", " + z + ")\",");
							middle.add("			\"from\": [" + modelFromX + ", " + modelFromY + ", " + modelFromZ + "],");
							middle.add("			\"to\": [" + modelToX + ", " + modelToY + ", " + modelToZ + "],");
							middle.add("			\"faces\": {");
							for (String face : faces) {
								middle.add("				\"" + face + "\": { \"texture\": \"#t\", \"uv\": [" + (u + 0.001) + ", " + (v + 0.001) + ", " + (u + 0.001) + ", " + (v + 0.001) + "] }" + (face.equals(faces[faces.length - 1]) ? "" : ","));
							}
							middle.add("			}");
							middle.add("		},");
						}
					}
				}
			}
		}
		
		// Remove the last comma
		String last = middle.get(middle.size() - 1);
		middle.set(middle.size() - 1, last.substring(0, last.length() - 1));
		
		String[] middleArray = new String[middle.size()];
		
		String[] end = {
				"	]",
				"}"
		};
		return ItemSet.chain(start, middle.toArray(middleArray), end);
	}

	@Override
	public String toString() {
		return "Sphere " + texture.getName();
	}
}
