package nl.knokko.customitems.block;

public class CustomBlock {

    private final int internalID;
    private CustomBlockValues values;

    public CustomBlock(int internalID, CustomBlockValues values) {
        this.internalID = internalID;
        this.values = new CustomBlockValues(values, false);
    }

    public int getInternalID() {
        return internalID;
    }

    public CustomBlockValues getValues() {
        return values;
    }

    public CustomBlockValues cloneValues() {
        return new CustomBlockValues(values, true);
    }

    public void setValues(CustomBlockValues newValues) {
        this.values = new CustomBlockValues(newValues, false);
    }
}
