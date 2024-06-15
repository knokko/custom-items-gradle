package nl.knokko.customitems.item.model;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class GeyserCustomModel {

    public static GeyserCustomModel load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("GeyserCustomModel", encoding);

        return new GeyserCustomModel(
                input.readString(), input.readByteArray(),
                input.readByteArray(), input.readByteArray(), input.readByteArray()
        );
    }

    public static AttachableParseResult parseAttachable(byte[] jsonBytes) {
        try {
            String jsonText = new String(jsonBytes, StandardCharsets.UTF_8);

            Object jsonObject = Jsoner.deserialize(jsonText);
            if (!(jsonObject instanceof JsonObject)) {
                return new AttachableParseResult("Attachable file doesn't seem to have a root JSON object");
            }
            JsonObject json = (JsonObject) jsonObject;

            Object attachableObject = json.get("minecraft:attachable");
            if (!(attachableObject instanceof JsonObject)) {
                return new AttachableParseResult("Root JSON object doesn't seem to have an attachable object");
            }

            Object descriptionObject = ((JsonObject) attachableObject).get("description");
            if (!(descriptionObject instanceof JsonObject)) {
                return new AttachableParseResult("Attachable JSON object doesn't seem to have a description object");
            }
            JsonObject description = (JsonObject) descriptionObject;

            Object identifierObject = description.get("identifier");
            if (!(identifierObject instanceof String)) {
                return new AttachableParseResult("Description JSON doesn't seem to have an identifier string");
            }
            String identifierString = (String) identifierObject;
            if (!identifierString.startsWith("geyser_custom:")) {
                return new AttachableParseResult("Identifier doesn't start with 'geyser_custom:'");
            }
            String identifier = identifierString.substring("geyser_custom:".length());

            Object texturesObject = description.get("textures");
            if (!(texturesObject instanceof JsonObject)) {
                return new AttachableParseResult("Description JSON doesn't seem to have a textures object");
            }
            JsonObject textures = (JsonObject) texturesObject;

            if (textures.containsKey("default")) {
                textures.put("default", "textures/kci/models/" + identifier);
            }

            String newString = Jsoner.prettyPrint(json.toJson());
            byte[] newBytes = newString.getBytes(StandardCharsets.UTF_8);

            return new AttachableParseResult(identifier, newBytes);
        } catch (JsonException e) {
            return new AttachableParseResult("Attachable file doesn't seem to be valid JSON");
        }
    }

    public final String attachableId;
    public final byte[] animationFile;
    public final byte[] attachableFile;
    public final byte[] modelFile;
    public final byte[] textureFile;

    public GeyserCustomModel(
            String attachableId, byte[] animationFile,
            byte[] attachableFile, byte[] modelFile, byte[] textureFile
    ) {
        this.attachableId = Objects.requireNonNull(attachableId);
        this.animationFile = Objects.requireNonNull(animationFile);
        this.attachableFile = Objects.requireNonNull(attachableFile);
        this.modelFile = Objects.requireNonNull(modelFile);
        this.textureFile = Objects.requireNonNull(textureFile);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);
        output.addString(attachableId);
        output.addByteArray(animationFile);
        output.addByteArray(attachableFile);
        output.addByteArray(modelFile);
        output.addByteArray(textureFile);
    }

    public static class AttachableParseResult {

        public final String error;
        public final String id;
        public final byte[] newJsonBytes;

        AttachableParseResult(String error) {
            this.error = error;
            this.id = null;
            this.newJsonBytes = null;
        }

        AttachableParseResult(String id, byte[] newJsonBytes) {
            this.error = null;
            this.id = id;
            this.newJsonBytes = newJsonBytes;
        }
    }
}
