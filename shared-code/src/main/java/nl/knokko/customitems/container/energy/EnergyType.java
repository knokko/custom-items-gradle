package nl.knokko.customitems.container.energy;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.UUID;

public class EnergyType extends ModelValues {

    public static EnergyType load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("EnergyType", encoding);

        EnergyType result = new EnergyType(false);
        result.id = new UUID(input.readLong(), input.readLong());
        result.name = input.readString();
        result.forceShareWithOtherContainerTypes = input.readBoolean();
        result.forceShareWithOtherLocations = input.readBoolean();
        result.forceShareWithOtherStringHosts = input.readBoolean();
        result.forceShareWithOtherPlayers = input.readBoolean();
        result.minValue = input.readInt();
        result.maxValue = input.readInt();
        result.initialValue = input.readInt();
        return result;
    }

    private UUID id;
    private String name;

    private boolean forceShareWithOtherContainerTypes, forceShareWithOtherLocations,
            forceShareWithOtherStringHosts, forceShareWithOtherPlayers;

    private int minValue, maxValue, initialValue;

    public EnergyType(boolean mutable) {
        super(mutable);
        this.id = UUID.randomUUID();
        this.name = "";
        this.forceShareWithOtherContainerTypes = false;
        this.forceShareWithOtherLocations = false;
        this.forceShareWithOtherStringHosts = false;
        this.forceShareWithOtherPlayers = false;
        this.minValue = 0;
        this.maxValue = 100;
        this.initialValue = 0;
    }

    public EnergyType(EnergyType toCopy, boolean mutable) {
        super(mutable);
        this.id = toCopy.getId();
        this.name = toCopy.getName();
        this.forceShareWithOtherContainerTypes = toCopy.shouldForceShareWithOtherContainerTypes();
        this.forceShareWithOtherLocations = toCopy.shouldForceShareWithOtherLocations();
        this.forceShareWithOtherStringHosts = toCopy.shouldForceShareWithOtherStringHosts();
        this.forceShareWithOtherPlayers = toCopy.shouldForceShareWithOtherPlayers();
        this.minValue = toCopy.getMinValue();
        this.maxValue = toCopy.getMaxValue();
        this.initialValue = toCopy.getInitialValue();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addLong(id.getMostSignificantBits());
        output.addLong(id.getLeastSignificantBits());
        output.addString(name);
        output.addBoolean(forceShareWithOtherContainerTypes);
        output.addBoolean(forceShareWithOtherLocations);
        output.addBoolean(forceShareWithOtherStringHosts);
        output.addBoolean(forceShareWithOtherPlayers);
        output.addInt(minValue);
        output.addInt(maxValue);
        output.addInt(initialValue);
    }

    @Override
    public EnergyType copy(boolean mutable) {
        return new EnergyType(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EnergyType) {
            EnergyType otherEnergy = (EnergyType) other;
            return this.id.equals(otherEnergy.id) && this.name.equals(otherEnergy.name)
                    && this.forceShareWithOtherContainerTypes == otherEnergy.forceShareWithOtherContainerTypes
                    && this.forceShareWithOtherLocations == otherEnergy.forceShareWithOtherLocations
                    && this.forceShareWithOtherStringHosts == otherEnergy.forceShareWithOtherStringHosts
                    && this.forceShareWithOtherPlayers == otherEnergy.forceShareWithOtherPlayers
                    && this.minValue == otherEnergy.minValue && this.maxValue == otherEnergy.maxValue
                    && this.initialValue == otherEnergy.initialValue;
        } else {
            return false;
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean shouldForceShareWithOtherContainerTypes() {
        return forceShareWithOtherContainerTypes;
    }

    public boolean shouldForceShareWithOtherLocations() {
        return forceShareWithOtherLocations;
    }

    public boolean shouldForceShareWithOtherStringHosts() {
        return forceShareWithOtherStringHosts;
    }

    public boolean shouldForceShareWithOtherPlayers() {
        return forceShareWithOtherPlayers;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public EnergyType recreateId() {
        assertMutable();
        this.id = UUID.randomUUID();
        return this;
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
    }

    public void setForceShareWithOtherContainerTypes(boolean forceShareWithOtherContainerTypes) {
        assertMutable();
        this.forceShareWithOtherContainerTypes = forceShareWithOtherContainerTypes;
    }

    public void setForceShareWithOtherLocations(boolean forceShareWithOtherLocations) {
        assertMutable();
        this.forceShareWithOtherLocations = forceShareWithOtherLocations;
    }

    public void setForceShareWithOtherPlayers(boolean forceShareWithOtherPlayers) {
        assertMutable();
        this.forceShareWithOtherPlayers = forceShareWithOtherPlayers;
    }

    public void setForceShareWithOtherStringHosts(boolean forceShareWithOtherStringHosts) {
        assertMutable();
        this.forceShareWithOtherStringHosts = forceShareWithOtherStringHosts;
    }

    public void setMinValue(int minValue) {
        assertMutable();
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        assertMutable();
        this.maxValue = maxValue;
    }

    public void setInitialValue(int initialValue) {
        assertMutable();
        this.initialValue = initialValue;
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (id == null) throw new ProgrammingValidationException("No ID");
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name is empty");
        if (minValue >= maxValue) throw new ValidationException("Minimum value must be smaller than maximum value");
        if (initialValue < minValue || initialValue > maxValue) {
            throw new ValidationException("Initial value must be between the minimum value and maximum value");
        }
    }

    public void validateComplete(ItemSet itemSet, UUID oldID) throws ValidationException, ProgrammingValidationException {
        validateIndependent();
        if (oldID != null && !oldID.equals(id)) throw new ProgrammingValidationException("Can't change the ID");
        if (oldID == null && itemSet.energyTypes.get(id).isPresent()) {
            throw new ProgrammingValidationException("Energy type with this ID already exists");
        }
        if (itemSet.energyTypes.stream().anyMatch(type -> !type.getId().equals(id) && type.getName().equals(name))) {
            throw new ValidationException("Energy type with name " + name + " already exists");
        }
    }
}
