package nl.knokko.customitems.projectile.cover;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.util.Checks.isClose;
import static nl.knokko.customitems.util.ResourceHelper.chain;

public class SphereProjectileCover extends ProjectileCover {

    static SphereProjectileCover load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        SphereProjectileCover result = new SphereProjectileCover(false);

        if (encoding == ENCODING_SPHERE1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("SphereProjectileCover", encoding);
        }

        return result;
    }

    private TextureReference texture;
    private int slotsPerAxis;
    private double scale;

    public SphereProjectileCover(boolean mutable) {
        super(mutable);
        this.texture = null;
        this.slotsPerAxis = 10;
        this.scale = 0.35;
    }

    public SphereProjectileCover(SphereProjectileCover toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.texture = toCopy.getTextureReference();
        this.slotsPerAxis = toCopy.getSlotsPerAxis();
        this.scale = toCopy.getScale();
    }

    private void load1(BitInput input, ItemSet itemSet) {
        loadSharedProperties1(input);
        this.texture = itemSet.textures.getReference(input.readString());
        this.slotsPerAxis = input.readInt();
        this.scale = input.readDouble();
    }

    @Override
    protected void save(BitOutput output) {
        output.addByte(ENCODING_SPHERE1);
        saveSharedProperties1(output);
        output.addString(texture.get().getName());
        output.addInt(slotsPerAxis);
        output.addDouble(scale);
    }

    protected boolean areSpherePropertiesEqual(SphereProjectileCover other) {
        return areBasePropertiesEqual(other) && this.slotsPerAxis == other.slotsPerAxis && isClose(this.scale, other.scale);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == SphereProjectileCover.class && areSpherePropertiesEqual((SphereProjectileCover) other);
    }

    @Override
    public SphereProjectileCover copy(boolean mutable) {
        return new SphereProjectileCover(this, mutable);
    }

    public KciTexture getTexture() {
        return texture == null ? null : texture.get();
    }

    public TextureReference getTextureReference() {
        return texture;
    }

    public int getSlotsPerAxis() {
        return slotsPerAxis;
    }

    public double getScale() {
        return scale;
    }

    public void setTexture(TextureReference newTexture) {
        assertMutable();
        Checks.notNull(newTexture);
        this.texture = newTexture;
    }

    public void setSlotsPerAxis(int newSlotsPerAxis) {
        assertMutable();
        this.slotsPerAxis = newSlotsPerAxis;
    }

    public void setScale(double newScale) {
        assertMutable();
        this.scale = newScale;
    }

    @Override
    public void validate(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validate(itemSet, oldName);
        if (texture == null) throw new ValidationException("You need to choose a texture");
        if (!itemSet.textures.isValid(texture)) throw new ProgrammingValidationException("Texture is no longer valid");
        if (slotsPerAxis <= 0) throw new ValidationException("The slots per axis must be positive");
        if (slotsPerAxis > 50) throw new ValidationException("The slots per axis can't be larger than 50");
        if (scale <= 0.0) throw new ValidationException("The scale must be positive");
    }

    @Override
    public void writeModel(ZipOutputStream output) {
        String[] model = createBulletModel(texture.get().getName(), slotsPerAxis, scale);

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
        return chain(start, middle.toArray(middleArray), end);
    }
}
