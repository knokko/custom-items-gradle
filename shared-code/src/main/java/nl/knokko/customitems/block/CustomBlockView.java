package nl.knokko.customitems.block;

public class CustomBlockView {

    private final CustomBlock block;

    public CustomBlockView(CustomBlock block) {
        this.block = block;
    }

    public int getInternalID() {
        return block.getInternalID();
    }

    public CustomBlockValues getValues() {
        return block.getValues();
    }
}
