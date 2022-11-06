package nl.knokko.customitems.nms;

public class UnknownMaterialException extends RuntimeException {

    private static final long serialVersionUID = 848362282745323817L;

    private final String materialName;

    public UnknownMaterialException(String materialName) {
        super("Unknown item name: " + materialName);
        this.materialName = materialName;
    }

    public String getMaterialName() {
        return materialName;
    }
}
