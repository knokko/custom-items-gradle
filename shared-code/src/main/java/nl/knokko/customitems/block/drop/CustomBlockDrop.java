package nl.knokko.customitems.block.drop;

import nl.knokko.customitems.recipe.OutputTable;

public class CustomBlockDrop {

    private RequiredItems requiredItems;
    private SilkTouchRequirement silkTouch;
    private OutputTable itemsToDrop;

    private final boolean mutable;

    public CustomBlockDrop(boolean mutable) {
        this.mutable = mutable;

        this.requiredItems = new RequiredItems(false);
        this.silkTouch = SilkTouchRequirement.OPTIONAL;
        this.itemsToDrop = new OutputTable();
    }

    public CustomBlockDrop(CustomBlockDrop toCopy, boolean mutable) {
        this(mutable);

        // TODO Copy the values
    }

}
