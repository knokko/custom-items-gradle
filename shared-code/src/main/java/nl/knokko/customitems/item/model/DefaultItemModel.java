package nl.knokko.customitems.item.model;

import nl.knokko.customitems.item.CustomItemValues;

import java.io.PrintWriter;
import java.util.zip.ZipOutputStream;

public class DefaultItemModel implements ItemModel {

    private final String parent;

    public DefaultItemModel(String parent) {
        this.parent = parent;
    }

    @Override
    public void write(ZipOutputStream zipOutput, CustomItemValues item, DefaultModelType defaultModelType) {
        PrintWriter jsonWriter = new PrintWriter(zipOutput);
        jsonWriter.println("{");
        jsonWriter.println("  \"parent\": \"" + this.parent + "\",");
        jsonWriter.println("  \"textures\": {");
        jsonWriter.print("    \"layer0\": \"customitems/" + item.getTexture().getName() + "\"");
        if (item.getItemType().isLeatherArmor()) {
            jsonWriter.print(",");
        }
        jsonWriter.println();
        if (item.getItemType().isLeatherArmor()) {
            jsonWriter.println("    \"layer1\": \"customitems/" + item.getTexture().getName() + "\"");
        }
        DisplayProperties[] display = determineDefaultDisplayProperties(defaultModelType);
        if (display.length > 0) {
            jsonWriter.println("  },");
            jsonWriter.println("  \"display\": {");
            for (int displayIndex = 0; displayIndex < display.length; displayIndex++) {
                DisplayProperties dp = display[displayIndex];
                jsonWriter.println("    \"" + dp.display + "\": {");
                jsonWriter.println("      \"rotation\": [ " + dp.rotationX + ", " + dp.rotationY + ", " + dp.rotationZ + " ],");
                jsonWriter.println("      \"translation\": [ " + dp.translationX + ", " + dp.translationY + ", " + dp.translationZ + " ],");
                jsonWriter.println("      \"scale\": [ " + dp.scaleX + ", " + dp.scaleY + ", " + dp.scaleZ + " ]");
                jsonWriter.print("    }");
                if (displayIndex != display.length - 1) {
                    jsonWriter.print(",");
                }
                jsonWriter.println();
            }
        }
        jsonWriter.println("  }");
        jsonWriter.println();
        jsonWriter.flush();
    }

    // TODO Add a unit test for this: probably some JSON-based zip comparison
    private DisplayProperties[] determineDefaultDisplayProperties(DefaultModelType defaultModelType) {
        switch (defaultModelType) {
            case BASIC: return new DisplayProperties[0];
            case SHIELD: return new DisplayProperties[] {
                    new DisplayProperties(
                            "thirdperson_righthand", 0f, -90f, 0f,
                            3f, -1.5f, 6f, 1.25f, 1.25f, 1.25f
                    ),
                    new DisplayProperties(
                            "thirdperson_lefthand", 0f, -90f, 0f,
                            3f, -2f, 4f, 1.25f, 1.25f, 1.25f
                    ),
                    new DisplayProperties(
                            "firstperson_righthand", -5f, 0f, -5f,
                            -2f, -5f, 0f, 1.35f, 1.35f, 1.35f
                    ),
                    new DisplayProperties(
                            "firstperson_lefthand", 5f, 0f, -5f,
                            -1.5f, -5f, 0f, 1.35f, 1.35f, 1.35f
                    )
            };
            case SHIELD_BLOCKING: return new DisplayProperties[] {
                    new DisplayProperties(
                            "thirdperson_righthand", 35f, -45f, -5f,
                            5f, 0f, 1f, 1.15f, 1.15f, 1.15f
                    ),
                    new DisplayProperties(
                            "thirdperson_lefthand", 35f, -35f, 5f,
                            3f, -3f, 1f, 1.25f, 1.25f, 1.25f
                    ),
                    new DisplayProperties(
                            "firstperson_righthand", 0f, -5f, 5f,
                            -6f, -0.5f, 0f, 1.2f, 1.2f, 1.2f
                    ),
                    new DisplayProperties(
                            "firstperson_lefthand", 0f, -5f, 5f,
                            -6f, -2.5f, 0f, 1.2f, 1.2f, 1.2f
                    )
            };
            case TRIDENT: return new DisplayProperties[] {
                    new DisplayProperties(
                            "gui", 0f, 0f, -45f,
                            0f, 0f, 0f, 1f, 1f, 1f
                    ),
                    new DisplayProperties(
                            "ground", 0f, 0f, -45f,
                            0f, 0f, 0f, 0.5f, 0.5f, 0.5f
                    )
            };
            case TRIDENT_IN_HAND: return new DisplayProperties[] {
                    new DisplayProperties(
                            "thirdperson_righthand", 0f, 65f, 0f,
                            0f, 0f, 0f, 0.5f, 1.8f, 1f
                    ),
                    new DisplayProperties(
                            "thirdperson_lefthand", 0f, 65f, 0f,
                            0f, 0f, 0f, 0.5f, 1.8f, 1f
                    ),
                    new DisplayProperties(
                            "firstperson_righthand", -30f, 100f, 0f,
                            4f, 2f, 0f, 0.5f, 1f, 1f
                    ),
                    new DisplayProperties(
                            "firstperson_lefthand", -30f, 100f, 0f,
                            4f, 2f, 0f, 0.5f, 1f, 1f
                    )
            };
            case TRIDENT_THROWING: return new DisplayProperties[] {
                    new DisplayProperties(
                            "thirdperson_righthand", 0f, 90f, 180f,
                            1f, -3f, 2f, 1f, 2f, 1f
                    ),
                    new DisplayProperties(
                            "thirdperson_lefthand", 0f, 90f, 180f,
                            1f, -3f, 2f, 1f, 2f, 1f
                    ),
                    new DisplayProperties(
                            "firstperson_righthand", -20f, -90f, 0f,
                            5f, 2f, -1f, 1f, 2f, 1f
                    ),
                    new DisplayProperties(
                            "firstperson_lefthand", -20f, -90f, 0f,
                            5f, 2f, -1f, 1f, 2f, 1f
                    )
            };
            default: throw new IllegalArgumentException("Unexpected model type " + defaultModelType);
        }
    }

    private static class DisplayProperties {

        final String display;
        final float rotationX, rotationY, rotationZ;
        final float translationX, translationY, translationZ;
        final float scaleX, scaleY, scaleZ;

        DisplayProperties(
                String display, float rotationX, float rotationY, float rotationZ,
                float translationX, float translationY, float translationZ, float scaleX, float scaleY, float scaleZ
        ) {
            this.display = display;
            this.translationX = translationX;
            this.translationY = translationY;
            this.translationZ = translationZ;
            this.rotationX = rotationX;
            this.rotationY = rotationY;
            this.rotationZ = rotationZ;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            this.scaleZ = scaleZ;
        }
    }
}
