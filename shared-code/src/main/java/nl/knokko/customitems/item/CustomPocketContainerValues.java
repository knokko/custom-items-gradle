package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ContainerReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.HashSet;
import java.util.Set;

public class CustomPocketContainerValues extends CustomItemValues {

    static CustomPocketContainerValues load(
            BitInput input, byte encoding, SItemSet itemSet
    ) throws UnknownEncodingException {
        // Note: Initial item type doesn't matter because it will be overwritten during loading
        CustomPocketContainerValues result = new CustomPocketContainerValues(false, CustomItemType.DIAMOND_HOE);

        if (encoding == ItemEncoding.ENCODING_POCKET_CONTAINER_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else {
            throw new UnknownEncodingException("PocketContainer", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private Set<ContainerReference> containers;

    public CustomPocketContainerValues(boolean mutable, CustomItemType initialItemType) {
        super(mutable, initialItemType);

        this.containers = new HashSet<>();
    }

    public CustomPocketContainerValues(CustomPocketContainerValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.containers = toCopy.getContainers();
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_POCKET_CONTAINER_10);
        save10(output);

        if (side == SItemSet.Side.EDITOR) {
            saveEditorOnlyProperties1(output);
        }
    }

    private void load10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadItemFlags6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
        loadPocketContainerOnlyProperties10(input, itemSet);
    }

    private void save10(BitOutput output) {
        saveBase10(output);
        savePocketContainerOnlyProperties10(output);
    }

    private void loadPocketContainerOnlyProperties10(BitInput input, SItemSet itemSet) {
        int numContainers = input.readInt();
        this.containers = new HashSet<>(numContainers);

        for (int counter = 0; counter < numContainers; counter++) {
            this.containers.add(itemSet.getContainerReference(input.readString()));
        }
    }

    private void savePocketContainerOnlyProperties10(BitOutput output) {
        output.addInt(containers.size());
        for (ContainerReference reference : containers) {
            output.addString(reference.get().getName());
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

    @Override
    public ModelValues copy(boolean mutable) {
        return new CustomPocketContainerValues(this, mutable);
    }

    public Set<ContainerReference> getContainers() {
        return new HashSet<>(containers);
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
    public void validateComplete(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        for (ContainerReference reference : containers) {
            if (!itemSet.isReferenceValid(reference)) throw new ProgrammingValidationException("A container is no longer valid");
        }
    }
}
