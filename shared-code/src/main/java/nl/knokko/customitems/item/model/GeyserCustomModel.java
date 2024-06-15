package nl.knokko.customitems.item.model;

public class GeyserCustomModel {

    public final String attachableId;
    public final byte[] animationFile;
    public final byte[] attachableFile;
    public final byte[] modelFile;

    public GeyserCustomModel(String attachableId, byte[] animationFile, byte[] attachableFile, byte[] modelFile) {
        this.attachableId = attachableId;
        this.animationFile = animationFile;
        this.attachableFile = attachableFile;
        this.modelFile = modelFile;
    }
}
