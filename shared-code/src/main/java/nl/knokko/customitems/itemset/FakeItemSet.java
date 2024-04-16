package nl.knokko.customitems.itemset;

import java.util.ArrayList;

/**
 * This is a dirty subclass of ItemSet that deletes all references and never finishes loading. It's useful for
 * reconstructing legacy items from their binary data since their referenced objects may be gone.
 */
public class FakeItemSet extends ItemSet {
    public FakeItemSet() {
        super(Side.PLUGIN);

        this.intReferences = new ArrayList<>();
        this.stringReferences = new ArrayList<>();
        this.uuidReferences = new ArrayList<>();
        this.finishedLoading = false;
    }
}
