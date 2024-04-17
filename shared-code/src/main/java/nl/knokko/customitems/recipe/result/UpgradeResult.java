package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.UpgradeReference;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.*;

import static java.lang.Math.abs;
import static nl.knokko.customitems.util.Checks.isClose;

public class UpgradeResult extends KciResult {

    public static UpgradeResult load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("UpgradeResult", encoding);

        UpgradeResult result = new UpgradeResult(false);
        result.ingredientIndex = input.readInt();
        result.inputSlotName = input.readString();
        result.upgrades = Collections.unmodifiableList(CollectionHelper.load(input, innerInput ->
                itemSet.upgrades.getReference(new UUID(innerInput.readLong(), innerInput.readLong()))
        ));
        result.repairPercentage = input.readFloat();
        if (input.readBoolean()) result.newType = KciResult.load(input, itemSet);
        else result.newType = null;
        result.keepOldUpgrades = input.readBoolean();
        result.keepOldEnchantments = input.readBoolean();
        return result;
    }

    private int ingredientIndex; // Only applicable to recipes in the crafting table
    private String inputSlotName; // Only applicable to container recipes

    private Collection<UpgradeReference> upgrades;
    private float repairPercentage;
    private KciResult newType;
    private boolean keepOldUpgrades;
    private boolean keepOldEnchantments;

    public UpgradeResult(boolean mutable) {
        super(mutable);
        this.ingredientIndex = -1;
        this.inputSlotName = null;

        this.upgrades = Collections.emptyList();
        this.repairPercentage = 0f;
        this.newType = null;
        this.keepOldUpgrades = true;
        this.keepOldEnchantments = true;
    }

    public UpgradeResult(UpgradeResult toCopy, boolean mutable) {
        super(mutable);
        this.ingredientIndex = toCopy.getIngredientIndex();
        this.inputSlotName = toCopy.getInputSlotName();

        this.upgrades = toCopy.getUpgrades();
        this.repairPercentage = toCopy.getRepairPercentage();
        this.newType = toCopy.getNewType();
        this.keepOldUpgrades = toCopy.shouldKeepOldUpgrades();
        this.keepOldEnchantments = toCopy.shouldKeepOldEnchantments();
    }

    @Override
    public UpgradeResult copy(boolean mutable) {
        return new UpgradeResult(this, mutable);
    }

    @Override
    public String toString() {
        return upgrades.size() + " upgrades";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof UpgradeResult) {
            UpgradeResult otherUpgrade = (UpgradeResult) other;
            return this.ingredientIndex == otherUpgrade.ingredientIndex
                    && Objects.equals(this.inputSlotName, otherUpgrade.inputSlotName)
                    && this.upgrades.equals(otherUpgrade.upgrades)
                    && isClose(this.repairPercentage, otherUpgrade.repairPercentage)
                    && Objects.equals(this.newType, otherUpgrade.newType)
                    && this.keepOldUpgrades == otherUpgrade.keepOldUpgrades
                    && this.keepOldEnchantments == otherUpgrade.keepOldEnchantments;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.ingredientIndex + 13 * Objects.hashCode(this.inputSlotName) + 71 * this.upgrades.hashCode()
                + 347 * Objects.hashCode(this.newType) + 1023 * Float.hashCode(this.repairPercentage)
                + (keepOldUpgrades ? 113 : 0) + (keepOldEnchantments ? 127 : 0);
    }

    @Override
    public List<String> getInfo() {
        List<String> info = new ArrayList<>();
        for (UpgradeReference upgrade : upgrades) {
            info.add("Add upgrade " + upgrade.get().getName());
        }
        if (abs(repairPercentage) > 0.0001f) info.add("Repairs " + repairPercentage + "%");
        if (newType != null) info.add("Turns ingredient into " + newType);
        if (keepOldUpgrades) info.add("Keeps old upgrades");
        else info.add("Loses old upgrades");
        if (keepOldEnchantments) info.add("Keeps old enchantments");
        else info.add("Loses old enchantments");
        return info;
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Result.UPGRADE);
        output.addByte((byte) 1);

        output.addInt(ingredientIndex);
        output.addString(inputSlotName);

        CollectionHelper.save(upgrades, ref -> {
            output.addLong(ref.get().getId().getMostSignificantBits());
            output.addLong(ref.get().getId().getLeastSignificantBits());
        }, output);
        output.addFloat(repairPercentage);
        output.addBoolean(newType != null);
        if (newType != null) newType.save(output);
        output.addBoolean(keepOldUpgrades);
        output.addBoolean(keepOldEnchantments);
    }

    public int getIngredientIndex() {
        return ingredientIndex;
    }

    public String getInputSlotName() {
        return inputSlotName;
    }

    public Collection<UpgradeReference> getUpgrades() {
        return upgrades;
    }

    public float getRepairPercentage() {
        return repairPercentage;
    }

    public KciResult getNewType() {
        return newType;
    }

    public boolean shouldKeepOldUpgrades() {
        return keepOldUpgrades;
    }

    public boolean shouldKeepOldEnchantments() {
        return keepOldEnchantments;
    }

    public void setIngredientIndex(int ingredientIndex) {
        assertMutable();
        this.ingredientIndex = ingredientIndex;
    }

    public void setInputSlotName(String inputSlotName) {
        assertMutable();
        this.inputSlotName = inputSlotName;
    }

    public void setUpgrades(Collection<UpgradeReference> upgrades) {
        assertMutable();
        this.upgrades = Collections.unmodifiableList(new ArrayList<>(upgrades));
    }

    public void setRepairPercentage(float repairPercentage) {
        assertMutable();
        this.repairPercentage = repairPercentage;
    }

    public void setNewType(KciResult newType) {
        assertMutable();
        this.newType = newType;
    }

    public void setKeepOldUpgrades(boolean keepOldUpgrades) {
        assertMutable();
        this.keepOldUpgrades = keepOldUpgrades;
    }

    public void setKeepOldEnchantments(boolean keepOldEnchantments) {
        assertMutable();
        this.keepOldEnchantments = keepOldEnchantments;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (ingredientIndex == -1 && inputSlotName == null) {
            throw new ValidationException("You need to choose an ingredient");
        }
        if (ingredientIndex != -1 && inputSlotName != null) {
            throw new ProgrammingValidationException("2 input slots are referenced");
        }
        if (ingredientIndex < -1) throw new ProgrammingValidationException("ingredient index must be at least -1");
        if (inputSlotName != null && inputSlotName.isEmpty()) {
            throw new ProgrammingValidationException("input slot name can't be empty");
        }

        if (upgrades == null) throw new ProgrammingValidationException("No upgrades");
        if (upgrades.contains(null)) throw new ProgrammingValidationException("Missing an upgrade");

        if (!Float.isFinite(repairPercentage)) throw new ValidationException("Repair percentage must be finite");

        if (newType != null) {
            if (newType instanceof UpgradeResult) {
                throw new ProgrammingValidationException("New type can't be another upgrade");
            }
            Validation.scope("New type", newType::validateIndependent);
        }
    }

    @Override
    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        for (UpgradeReference upgrade : upgrades) {
            if (!itemSet.upgrades.isValid(upgrade)) throw new ProgrammingValidationException("Upgrade is no longer valid");
        }

        if (newType != null) {
            Validation.scope("New type", newType::validateComplete, itemSet);
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException {
        if (newType != null) newType.validateExportVersion(version);
    }
}
