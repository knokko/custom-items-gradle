package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.BowTexture;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.texture.animated.AnimatedTexture;
import nl.knokko.customitems.texture.animated.AnimationFrame;
import nl.knokko.customitems.texture.animated.AnimationImage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

class GeyserPackControllerGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    GeyserPackControllerGenerator(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void generateBow() throws IOException {
        if (itemSet.textures.stream().anyMatch(texture -> texture instanceof BowTexture)) {
            IOHelper.propagate(
                    "kci_bow.render_controllers.json", zipOutput,
                    "render_controllers/kci/bow.render_controllers.json", null
            );
        }
    }

    static Map<String, Integer> createFrameMap(AnimatedTexture at) {
        Map<String, Integer> frameMap = new HashMap<>();
        for (AnimationImage frame : at.getImageReferences()) {
            if (!frameMap.containsKey(frame.getLabel())) {
                frameMap.put(frame.getLabel(), 1 + frameMap.size());
            }
        }

        return frameMap;
    }

    void generateAnimations() throws IOException {
        for (KciTexture texture : itemSet.textures) {
            if (texture instanceof AnimatedTexture) {
                AnimatedTexture at = (AnimatedTexture) texture;

                Map<String, Integer> frameMap = createFrameMap(at);

                long duration = 0;
                for (AnimationFrame frame : at.getFrames()) duration += frame.getDuration();

                StringBuilder textureFunction = new StringBuilder();
                textureFunction.append("temp.mod_time = math.mod(20.0 * query.life_time, ")
                                .append((double) duration).append(");");

                long passedTime = 0;
                AnimationFrame lastFrame = at.getFrames().get(at.getFrames().size() - 1);
                for (AnimationFrame frame : at.getFrames()) {
                    if (frame != lastFrame) {
                        passedTime += frame.getDuration();
                        textureFunction.append(" temp.mod_time < ")
                                .append((double) passedTime).append(" ? { return texture.frame")
                                .append(frameMap.get(frame.getImageLabel())).append("; };");
                    }
                }

                textureFunction.append(" return texture.frame")
                        .append(frameMap.get(lastFrame.getImageLabel())).append(';');

                IOHelper.propagate(
                        "animated_template.render_controller.json", zipOutput,
                        "render_controllers/kci/animated/" + at.getName() + ".render_controller.json",
                        line -> line.replace("%TEXTURE_NAME%", at.getName())
                                .replace("%TEXTURE_FUNCTION%", textureFunction)
                );
            }
        }
    }
}
