package nl.knokko.customitems.container.energy;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.EnergyTypeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.UUID;

public class RecipeEnergyValues extends ModelValues {

    public static RecipeEnergyValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("RecipeEnergy", encoding);

        RecipeEnergyValues result = new RecipeEnergyValues(false);
        result.energyType = itemSet.energyTypes.getReference(new UUID(input.readLong(), input.readLong()));
        result.operation = RecipeEnergyOperation.valueOf(input.readString());
        result.amount = input.readInt();
        return result;
    }

    public static RecipeEnergyValues createQuick(
            EnergyTypeReference energyType, RecipeEnergyOperation operation, int amount
    ) {
        RecipeEnergyValues result = new RecipeEnergyValues(true);
        result.setEnergyType(energyType);
        result.setOperation(operation);
        result.setAmount(amount);
        return result;
    }

    private EnergyTypeReference energyType;
    private RecipeEnergyOperation operation;
    private int amount;

    public RecipeEnergyValues(boolean mutable) {
        super(mutable);
        this.energyType = null;
        this.operation = RecipeEnergyOperation.REQUIRE_AT_LEAST;
        this.amount = 100;
    }

    public RecipeEnergyValues(RecipeEnergyValues toCopy, boolean mutable) {
        super(mutable);
        this.energyType = toCopy.getEnergyTypeReference();
        this.operation = toCopy.getOperation();
        this.amount = toCopy.getAmount();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addLong(energyType.get().getId().getMostSignificantBits());
        output.addLong(energyType.get().getId().getLeastSignificantBits());
        output.addString(operation.name());
        output.addInt(amount);
    }

    @Override
    public RecipeEnergyValues copy(boolean mutable) {
        return new RecipeEnergyValues(this, mutable);
    }

    @Override
    public String toString() {
        return operation + " " + amount + " " + energyType.get().getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RecipeEnergyValues) {
            RecipeEnergyValues otherEnergy = (RecipeEnergyValues) other;
            return this.energyType.equals(otherEnergy.energyType) && this.operation == otherEnergy.operation
                    && this.amount == otherEnergy.amount;
        } else {
            return false;
        }
    }

    public EnergyTypeReference getEnergyTypeReference() {
        return energyType;
    }

    public EnergyTypeValues getEnergyType() {
        return energyType != null ? energyType.get() : null;
    }

    public RecipeEnergyOperation getOperation() {
        return operation;
    }

    public int getAmount() {
        return amount;
    }

    public void setEnergyType(EnergyTypeReference energyType) {
        assertMutable();
        Checks.notNull(energyType);
        this.energyType = energyType;
    }

    public void setOperation(RecipeEnergyOperation operation) {
        assertMutable();
        Checks.notNull(operation);
        this.operation = operation;
    }

    public void setAmount(int amount) {
        assertMutable();
        this.amount = amount;
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (energyType == null) throw new ValidationException("You must choose an energy type");
        if (operation == null) throw new ProgrammingValidationException("No operation");

        if (operation == RecipeEnergyOperation.DECREASE && amount <= 0) {
            throw new ValidationException("You can only decrease by positive amounts");
        }
        if (operation == RecipeEnergyOperation.INCREASE && amount <= 0) {
            throw new ValidationException("You can only increase by positive amounts");
        }
    }

    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (!itemSet.energyTypes.isValid(energyType)) {
            throw new ProgrammingValidationException("Energy type is invalid");
        }

        if (operation == RecipeEnergyOperation.REQUIRE_AT_LEAST && amount <= energyType.get().getMinValue()) {
            throw new ValidationException("There is always at least " + amount + " " + energyType.get().getName());
        }
        if (operation == RecipeEnergyOperation.REQUIRE_AT_LEAST && amount > energyType.get().getMaxValue()) {
            throw new ValidationException("Having at least " + amount + " " + energyType.get().getName() + " is impossible");
        }
        if (operation == RecipeEnergyOperation.REQUIRE_AT_MOST && amount < energyType.get().getMinValue()) {
            throw new ValidationException("Having at most " + amount + " " + energyType.get().getName() + " is impossible");
        }
        if (operation == RecipeEnergyOperation.REQUIRE_AT_MOST && amount >= energyType.get().getMaxValue()) {
            throw new ValidationException("There is always at most " + amount + " " + energyType.get().getName());
        }
    }
}
