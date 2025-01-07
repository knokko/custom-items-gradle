package nl.knokko.customitems.block.model;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipOutputStream;

public class SimpleBlockModel implements BlockModel {

    static SimpleBlockModel loadSimple(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("SimpleBlockModel", encoding);

        return new SimpleBlockModel(itemSet.textures.getReference(input.readString()));
    }

    private final TextureReference texture;

    public SimpleBlockModel(TextureReference texture) {
        this.texture = texture;
    }

    @Override
    public void write(ZipOutputStream zipOutput, String blockName) throws IOException {
        PrintWriter modelWriter = new PrintWriter(zipOutput);
        modelWriter.println("{");
        modelWriter.println("    \"parent\": \"block/cube_all\",");
        modelWriter.println("    \"textures\": {");
        modelWriter.println("        \"all\": \"customitems/" + texture.get().getName() + "\"");
        modelWriter.println("    }");
        modelWriter.println("}");
        modelWriter.flush();
    }

    @Override
    public void save(BitOutput output, ItemSet.Side targetSide) {
        output.addByte(MODEL_TYPE_SIMPLE);
        output.addByte((byte) 1);

        output.addString(texture.get().getName());
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (texture == null) throw new ValidationException("You need to select a texture");
        if (!itemSet.textures.isValid(texture)) throw new ProgrammingValidationException("Texture is no longer valid");
    }

    @Override
    public TextureReference getPrimaryTexture() {
        return texture;
    }

    @Override
    public String toString() {
        return "simple";
    }
}
