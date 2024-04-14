package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.ContainerReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Collection;
import java.util.Objects;

import static nl.knokko.customitems.container.slot.ContainerSlotValues.Encodings.LINK;

public class LinkSlotValues extends ContainerSlotValues {

    static LinkSlotValues loadLink(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        LinkSlotValues slot = new LinkSlotValues(false);
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("LinkSlot", encoding);

        slot.linkedContainer = itemSet.containers.getReference(input.readString());
        if (input.readBoolean()) slot.display = SlotDisplayValues.load(input, itemSet);
        else slot.display = null;

        return slot;
    }

    public static LinkSlotValues createQuick(ContainerReference linkedContainer, SlotDisplayValues display) {
        LinkSlotValues slot = new LinkSlotValues(true);
        slot.setLinkedContainer(linkedContainer);
        slot.setDisplay(display);
        return slot.copy(false);
    }

    private ContainerReference linkedContainer;
    private SlotDisplayValues display;

    public LinkSlotValues(boolean mutable) {
        super(mutable);
        this.linkedContainer = null;
        this.display = null;
    }

    public LinkSlotValues(LinkSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.linkedContainer = toCopy.getLinkedContainerReference();
        this.display = toCopy.getDisplay();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(LINK);
        output.addByte((byte) 1);

        output.addString(linkedContainer.get().getName());
        output.addBoolean(display != null);
        if (display != null) display.save(output);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof LinkSlotValues) {
            LinkSlotValues otherSlot = (LinkSlotValues) other;
            return this.linkedContainer.equals(otherSlot.linkedContainer) && Objects.equals(this.display, otherSlot.display);
        } else return false;
    }

    @Override
    public LinkSlotValues copy(boolean mutable) {
        return new LinkSlotValues(this, mutable);
    }

    @Override
    public ContainerSlotValues nonConflictingCopy(CustomContainerValues container) {
        return this.copy(true);
    }

    @Override
    public boolean canInsertItems() {
        return false;
    }

    @Override
    public boolean canTakeItems() {
        return false;
    }

    public CustomContainerValues getLinkedContainer() {
        return linkedContainer != null ? linkedContainer.get() : null;
    }

    public ContainerReference getLinkedContainerReference() {
        return linkedContainer;
    }

    public SlotDisplayValues getDisplay() {
        return display;
    }

    public void setLinkedContainer(ContainerReference linkedContainer) {
        assertMutable();
        this.linkedContainer = Objects.requireNonNull(linkedContainer);
    }

    public void setDisplay(SlotDisplayValues display) {
        assertMutable();
        this.display = display != null ? display.copy(false) : null;
    }

    @Override
    public void validate(ItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (linkedContainer == null) throw new ValidationException("You need to choose a container");
        if (!itemSet.containers.isValid(linkedContainer)) {
            throw new ProgrammingValidationException("Linked container is no longer valid");
        }
        if (display != null) {
            Validation.scope("Display", display::validate, itemSet);
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (display != null) {
            Validation.scope("Display", display::validateExportVersion, version);
        }
    }
}
