package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class ItemBridgeResult extends KciResult {

    public static ItemBridgeResult load(BitInput input) throws UnknownEncodingException {
        byte internalEncoding = input.readByte();
        if (internalEncoding != 1) throw new UnknownEncodingException("MimicResult", internalEncoding);

        ItemBridgeResult result = new ItemBridgeResult(false);
        result.itemId = input.readString();
        result.amount = input.readInt();
        return result;
    }

    public static ItemBridgeResult createQuick(String itemId, int amount) {
        ItemBridgeResult result = new ItemBridgeResult(true);
        result.setItemId(itemId);
        result.setAmount(amount);
        return result;
    }

    private String itemId;
    private int amount;

    public ItemBridgeResult(boolean mutable) {
        super(mutable);
        this.itemId = "minecraft:dirt";
        this.amount = 3;
    }

    public ItemBridgeResult(ItemBridgeResult toCopy, boolean mutable) {
        super(mutable);
        this.itemId = toCopy.getItemId();
        this.amount = toCopy.getAmount();
    }

    @Override
    public ItemBridgeResult copy(boolean mutable) {
        return new ItemBridgeResult(this, mutable);
    }

    @Override
    public String toString() {
        return "ItemBridge(" + this.itemId + " x " + this.amount + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ItemBridgeResult) {
            ItemBridgeResult otherResult = (ItemBridgeResult) other;
            return this.itemId.equals(otherResult.itemId) && this.amount == otherResult.amount;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.itemId.hashCode() + this.amount;
    }

    @Override
    public List<String> getInfo() {
        List<String> result = new ArrayList<>(2);
        result.add("ItemBridge item:");
        result.add(this.itemId + " x " + this.amount);
        return result;
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Result.ITEM_BRIDGE);
        output.addByte((byte) 1);
        output.addString(this.itemId);
        output.addInt(this.amount);
    }

    public String getItemId() {
        return this.itemId;
    }

    @Override
    public byte getAmount() {
        return (byte) this.amount;
    }

    @Override
    public byte guessMaxStackSize() {
        return 64;
    }

    public void setItemId(String itemId) {
        Checks.notNull(itemId);
        assertMutable();
        this.itemId = itemId;
    }

    public void setAmount(int amount) {
        assertMutable();
        this.amount = amount;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (this.itemId == null) throw new ProgrammingValidationException("No item id");
        if (!this.itemId.contains(":")) throw new ValidationException("Item id must contain ':'");
        if (this.amount < 1) throw new ValidationException("Amount must be positive");
        if (this.amount > 64) throw new ValidationException("Amount can be at most 64");
    }

    @Override
    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        this.validateIndependent();
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException {}
}
