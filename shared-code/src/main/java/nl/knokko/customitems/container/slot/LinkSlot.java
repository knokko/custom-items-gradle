package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.itemset.ContainerReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Collection;
import java.util.Objects;

import static nl.knokko.customitems.container.slot.ContainerSlot.Encodings.LINK;

public class LinkSlot extends ContainerSlot {

    static LinkSlot loadLink(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        LinkSlot slot = new LinkSlot(false);
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("LinkSlot", encoding);

        slot.linkedContainer = itemSet.containers.getReference(input.readString());
        if (input.readBoolean()) slot.display = SlotDisplay.load(input, itemSet);
        else slot.display = null;

        return slot;
    }

    public static LinkSlot createQuick(ContainerReference linkedContainer, SlotDisplay display) {
        LinkSlot slot = new LinkSlot(true);
        slot.setLinkedContainer(linkedContainer);
        slot.setDisplay(display);
        return slot.copy(false);
    }

    private ContainerReference linkedContainer;
    private SlotDisplay display;

    public LinkSlot(boolean mutable) {
        super(mutable);
        this.linkedContainer = null;
        this.display = null;
    }

    public LinkSlot(LinkSlot toCopy, boolean mutable) {
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
        if (other instanceof LinkSlot) {
            LinkSlot otherSlot = (LinkSlot) other;
            return this.linkedContainer.equals(otherSlot.linkedContainer) && Objects.equals(this.display, otherSlot.display);
        } else return false;
    }

    @Override
    public LinkSlot copy(boolean mutable) {
        return new LinkSlot(this, mutable);
    }

    @Override
    public ContainerSlot nonConflictingCopy(KciContainer container) {
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

    public KciContainer getLinkedContainer() {
        return linkedContainer != null ? linkedContainer.get() : null;
    }

    public ContainerReference getLinkedContainerReference() {
        return linkedContainer;
    }

    public SlotDisplay getDisplay() {
        return display;
    }

    public void setLinkedContainer(ContainerReference linkedContainer) {
        assertMutable();
        this.linkedContainer = Objects.requireNonNull(linkedContainer);
    }

    public void setDisplay(SlotDisplay display) {
        assertMutable();
        this.display = display != null ? display.copy(false) : null;
    }

    @Override
    public void validate(ItemSet itemSet, Collection<ContainerSlot> otherSlots) throws ValidationException, ProgrammingValidationException {
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
