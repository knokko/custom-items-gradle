package nl.knokko.customitems.item;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ContainerReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomPocketContainerValues extends CustomItemValues {

    static CustomPocketContainerValues load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        CustomPocketContainerValues result = new CustomPocketContainerValues(false);

        if (encoding == ItemEncoding.ENCODING_POCKET_CONTAINER_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_POCKET_CONTAINER_12) {
            result.loadPocketContainerPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("PocketContainer", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private Set<ContainerReference> containers;

    public CustomPocketContainerValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);

        this.containers = new HashSet<>();
    }

    public CustomPocketContainerValues(CustomPocketContainerValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.containers = toCopy.getContainerReferences();
    }

    protected void loadPocketContainerPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadSharedPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("PocketContainerNew", encoding);

        int numContainers = input.readInt();
        this.containers = new HashSet<>(numContainers);
        for (int counter = 0; counter < numContainers; counter++) {
            this.containers.add(itemSet.getContainerReference(input.readString()));
        }
    }

    protected void savePocketContainerPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        this.saveSharedPropertiesNew(output, targetSide);

        output.addByte((byte) 1);

        output.addInt(this.containers.size());
        for (ContainerReference containerRef : this.containers) {
            output.addString(containerRef.get().getName());
        }
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_POCKET_CONTAINER_12);
        this.savePocketContainerPropertiesNew(output, side);
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadBase10(input, itemSet);
        loadPocketContainerOnlyProperties10(input, itemSet);
    }

    private void loadPocketContainerOnlyProperties10(BitInput input, ItemSet itemSet) {
        int numContainers = input.readInt();
        this.containers = new HashSet<>(numContainers);

        for (int counter = 0; counter < numContainers; counter++) {
            this.containers.add(itemSet.getContainerReference(input.readString()));
        }
    }

    private void initDefaults10() {
        initBaseDefaults10();
        initPocketContainerOnlyDefaults10();
    }

    private void initPocketContainerOnlyDefaults10() {
        // Nothing to be done until the next encoding is known
    }

    @Override
    public byte getMaxStacksize() {
        return 1;
    }

    protected boolean arePocketContainerPropertiesEqual(CustomPocketContainerValues other) {
        return areBaseItemPropertiesEqual(other) && this.containers.equals(other.containers);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomPocketContainerValues.class && arePocketContainerPropertiesEqual((CustomPocketContainerValues) other);
    }

    @Override
    public CustomPocketContainerValues copy(boolean mutable) {
        return new CustomPocketContainerValues(this, mutable);
    }

    public Set<ContainerReference> getContainerReferences() {
        return new HashSet<>(containers);
    }

    public Set<CustomContainerValues> getContainers() {
        return containers.stream().map(ContainerReference::get).collect(Collectors.toSet());
    }

    public void setContainers(Set<ContainerReference> newContainers) {
        assertMutable();
        Checks.nonNull(newContainers);
        this.containers = new HashSet<>(newContainers);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (containers == null) throw new ProgrammingValidationException("No containers");
        if (containers.isEmpty()) throw new ValidationException("You must select at least 1 container");
        for (ContainerReference reference : containers) {
            if (reference == null) throw new ProgrammingValidationException("A container is missing");
        }
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        for (ContainerReference reference : containers) {
            if (!itemSet.isReferenceValid(reference)) throw new ProgrammingValidationException("A container is no longer valid");
        }
    }
}
