package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.item.CustomBlockItemValues;
import nl.knokko.customitems.item.CustomHelmet3dValues;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemValues;

public class DefaultItemModels {

    public static String[] getDefaultModel(CustomItemValues item) {
        if (item instanceof CustomBlockItemValues) {
            return createModelBlockItem(((CustomBlockItemValues) item).getBlock());
        }
        return getDefaultModel(
                item.getItemType(), item.getTexture().getName(),
                item.getItemType().isLeatherArmor(),
                !(item instanceof CustomHelmet3dValues)
        );
    }

    static String[] createModelBlockItem(CustomBlockValues block) {
        return new String[] {
                "{",
                "    \"parent\": \"customblocks/" + block.getName() + "\"",
                "}"
        };
    }

    public static String[] getDefaultModel(CustomItemType type, String textureName, boolean isLeather, boolean hasDefault) {
        if (!hasDefault) {
            return new String[] {
                    "There is no default model for this item type",
                    "because it requires a custom model. This is a",
                    "complex task, so only do this if you know what",
                    "you're doing."
            };
        } else if (type == CustomItemType.BOW) {
            return getDefaultModelBow(textureName);
        } else if (type == CustomItemType.CROSSBOW) {
            return getDefaultModelCrossbow(textureName);
        } else if (type == CustomItemType.SHIELD) {
            return getDefaultModelShield(textureName);
        } else if (type == CustomItemType.TRIDENT) {
            return getDefaultModelTrident(textureName);
        } else {
            String[] start = {
                    "{",
                    "    \"parent\": \"item/handheld\",",
                    "    \"textures\": {",
                    "        \"layer0\": \"customitems/" + textureName + "\"" + (isLeather ? "," : "")
            };

            String[] mid;
            if (isLeather) {
                mid = new String[] {"        \"layer1\": \"customitems/" + textureName + "\""};
            } else {
                mid = new String[0];
            }

            String[] end = {
                    "    }",
                    "}"
            };

            return chain(start, mid, end);
        }
    }

    public static String[] getDefaultModelBow(String textureName) {
        return new String[] {
                "{",
                "    \"parent\": \"item/bow\",",
                "    \"textures\": {",
                "        \"layer0\": \"customitems/" + textureName + "_standby\"",
                "    }",
                "}"
        };
    }

    public static String[] getDefaultModelCrossbow(String textureName) {
        return new String[] {
                "{",
                "    \"parent\": \"item/crossbow\",",
                "    \"textures\": {",
                "        \"layer0\": \"customitems/" + textureName + "_standby\"",
                "    }",
                "}"
        };
    }

    public static String[] getDefaultModelShield(String textureName) {
        return new String[] {
                "{",
                "    \"parent\": \"item/handheld\",",
                "    \"textures\": {",
                "        \"layer0\": \"customitems/" + textureName + "\"",
                "    },",
                "    \"display\": {",
                "        \"thirdperson_righthand\": {",
                "            \"rotation\": [0, -90, 0],",
                "            \"translation\": [3, -1.5, 6],",
                "            \"scale\": [1.25, 1.25, 1.25]",
                "        },",
                "        \"thirdperson_lefthand\": {",
                "            \"rotation\": [0, -90, 0],",
                "            \"translation\": [3, -2, 4],",
                "            \"scale\": [1.25, 1.25, 1.25]",
                "        },",
                "        \"firstperson_righthand\": {",
                "            \"rotation\": [-5, 0, -5],",
                "            \"translation\": [-2, -5, 0],",
                "            \"scale\": [1.35, 1.35, 1.35]",
                "        },",
                "        \"firstperson_lefthand\": {",
                "            \"rotation\": [5, 0, -5],",
                "            \"translation\": [-1.5, -5, 0],",
                "            \"scale\": [1.35, 1.35, 1.35]",
                "        }",
                "    }",
                "}"
        };
    }

    public static String[] getDefaultModelBlockingShield(String textureName) {
        return new String[] {
                "{",
                "    \"parent\": \"item/handheld\",",
                "    \"textures\": {",
                "        \"layer0\": \"customitems/" + textureName + "\"",
                "    },",
                "    \"display\": {",
                "        \"thirdperson_righthand\": {",
                "            \"rotation\": [35, -45, -5],",
                "            \"translation\": [5, 0, 1],",
                "            \"scale\": [1.15, 1.15, 1.15]",
                "        },",
                "        \"thirdperson_lefthand\": {",
                "            \"rotation\": [35, -35, -5],",
                "            \"translation\": [3, -3, -1],",
                "            \"scale\": [1.25, 1.25, 1.25]",
                "        },",
                "        \"firstperson_righthand\": {",
                "            \"rotation\": [0, -5, 5],",
                "            \"translation\": [-6, -0.5, 0],",
                "            \"scale\": [1.2, 1.2, 1.2]",
                "        },",
                "        \"firstperson_lefthand\": {",
                "            \"rotation\": [0, -5, 5],",
                "            \"translation\": [-6, -2.5, 0],",
                "            \"scale\": [1.2, 1.2, 1.2]",
                "        }",
                "    }",
                "}"
        };
    }

    public static String[] getDefaultModelTrident(String textureName) {
        return new String[] {
                "{",
                "    \"parent\": \"item/generated\",",
                "    \"textures\": {",
                "        \"layer0\": \"customitems/" + textureName + "\"",
                "    },",
                "    \"display\": {",
                "        \"gui\": {",
                "            \"rotation\": [0, 0, -45],",
                "            \"translation\": [0, 0, 0],",
                "            \"scale\": [1, 1, 1]",
                "        },",
                "        \"ground\": {",
                "            \"rotation\": [0, 0, -45],",
                "            \"translation\": [0, 0, 0],",
                "            \"scale\": [0.5, 0.5, 0.5]",
                "        }",
                "    }",
                "}"
        };
    }

    public static String[] getDefaultModelTridentInHand(String textureName) {
        return new String[] {
                "{",
                "    \"parent\": \"item/handheld\",",
                "    \"textures\": {",
                "        \"layer0\": \"customitems/" + textureName + "\"",
                "    },",
                "    \"display\": {",
                "        \"thirdperson_righthand\": {",
                "            \"rotation\": [0, 65, 0],",
                "            \"translation\": [0, 0, 0],",
                "            \"scale\": [0.5, 1.8, 1.0]",
                "        },",
                "        \"thirdperson_lefthand\": {",
                "            \"rotation\": [0, 65, 0],",
                "            \"translation\": [0, 0, 0],",
                "            \"scale\": [0.5, 1.8, 1.0]",
                "        },",
                "        \"firstperson_righthand\": {",
                "            \"rotation\": [-30, 100, 0],",
                "            \"translation\": [4, 2, 0],",
                "            \"scale\": [0.5, 1.0, 1.0]",
                "        },",
                "        \"firstperson_lefthand\": {",
                "            \"rotation\": [-30, 100, 0],",
                "            \"translation\": [4, 2, 0],",
                "            \"scale\": [0.5, 1.0, 1.0]",
                "        }",
                "    }",
                "}"
        };
    }

    public static String[] getDefaultModelTridentThrowing(String textureName) {
        return new String[] {
                "{",
                "    \"parent\": \"item/handheld\",",
                "    \"textures\": {",
                "        \"layer0\": \"customitems/" + textureName + "\"",
                "    },",
                "    \"display\": {",
                "        \"thirdperson_righthand\": {",
                "            \"rotation\": [0, 90, 180],",
                "            \"translation\": [1, -3, 2],",
                "            \"scale\": [1, 2, 1]",
                "        },",
                "        \"thirdperson_lefthand\": {",
                "            \"rotation\": [0, 90, 180],",
                "            \"translation\": [1, -3, 2],",
                "            \"scale\": [1, 2, 1]",
                "        },",
                "        \"firstperson_righthand\": {",
                "            \"rotation\": [-20, -90, 0],",
                "            \"translation\": [5, 2, -1],",
                "            \"scale\": [1, 2, 1]",
                "        },",
                "        \"firstperson_lefthand\": {",
                "            \"rotation\": [-20, -90, 0],",
                "            \"translation\": [5, 2, -1],",
                "            \"scale\": [1, 2, 1]",
                "        }",
                "    }",
                "}"
        };
    }

    static String[] getMinecraftModelTridentInHandBegin() {
        return new String[] {
                "{",
                "    \"parent\": \"builtin/entity\",",
                "    \"textures\": {",
                "        \"particle\": \"item/trident\"",
                "    },",
                "    \"display\": {",
                "        \"thirdperson_righthand\": {",
                "            \"rotation\": [0, 60, 0],",
                "            \"translation\": [11, 17, -2],",
                "            \"scale\": [1, 1, 1]",
                "        },",
                "        \"thirdperson_lefthand\": {",
                "            \"rotation\": [0, 60, 0],",
                "            \"translation\": [3, 17, 12],",
                "            \"scale\": [1, 1, 1]",
                "        },",
                "        \"firstperson_righthand\": {",
                "            \"rotation\": [0, -90, 25],",
                "            \"translation\": [-3, 17, 1],",
                "            \"scale\": [1, 1, 1]",
                "        },",
                "        \"firstperson_lefthand\": {",
                "            \"rotation\": [0, 90, -25],",
                "            \"translation\": [13, 17, 1],",
                "            \"scale\": [1, 1, 1]",
                "        },",
                "        \"gui\": {",
                "            \"rotation\": [15, -25, -5],",
                "            \"translation\": [2, 3, 0],",
                "            \"scale\": [0.65, 0.65, 0.65]",
                "        },",
                "        \"fixed\": {",
                "            \"rotation\": [0, 180, 0],",
                "            \"translation\": [-2, 4, -5],",
                "            \"scale\": [0.5, 0.5, 0.5]",
                "        },",
                "        \"ground\": {",
                "            \"rotation\": [0, 0, 0],",
                "            \"translation\": [4, 4, 2],",
                "            \"scale\": [0.25, 0.25, 0.25]",
                "        }",
                "    },",
                "    \"overrides\": [",
                "        {\"predicate\": {\"throwing\": 1}, \"model\": \"item/trident_throwing\"},",
        };
    }

    static String[] getMinecraftModelTridentInHandEnd() {
        return new String[] {
                "        {\"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/trident_in_hand\"},",
                "        {\"predicate\": {\"damaged\": 1, \"damage\": 0, \"throwing\": 1}, \"model\": \"item/trident_throwing\"}",
                "    ]",
                "}"
        };
    }

    public static String[] chain(String[]...arrays) {
        int length = 0;
        for (String[] array : arrays) {
            length += array.length;
        }
        String[] result = new String[length];
        int index = 0;
        for (String[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }
        return result;
    }
}
