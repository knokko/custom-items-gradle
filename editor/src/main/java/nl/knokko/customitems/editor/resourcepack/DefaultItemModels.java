package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.block.CustomBlockValues;

public class DefaultItemModels {

    static String[] createModelBlockItem(CustomBlockValues block) {
        return new String[] {
                "{",
                "    \"parent\": \"customblocks/" + block.getName() + "\"",
                "}"
        };
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
}
