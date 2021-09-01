package nl.knokko.customitems.block;

import nl.knokko.customitems.model.Model;

public class CustomBlock extends Model<CustomBlockValues> {

    private final int internalID;

    public CustomBlock(int internalID, CustomBlockValues values) {
        super(values);
        this.internalID = internalID;
    }

    public int getInternalID() {
        return internalID;
    }
}
