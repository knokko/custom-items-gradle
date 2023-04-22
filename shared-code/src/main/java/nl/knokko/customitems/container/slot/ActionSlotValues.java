package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Collection;
import java.util.Objects;

import static nl.knokko.customitems.container.slot.ContainerSlotValues.Encodings.ACTION;

public class ActionSlotValues extends ContainerSlotValues {

    static ActionSlotValues loadAction(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ActionSlot", encoding);

        ActionSlotValues slot = new ActionSlotValues(false);
        slot.actionID = input.readString();
        if (input.readBoolean()) slot.display = SlotDisplayValues.load(input, itemSet);
        else slot.display = null;
        return slot;
    }

    public static ActionSlotValues createQuick(String actionID, SlotDisplayValues display) {
        ActionSlotValues slot = new ActionSlotValues(true);
        slot.setActionID(actionID);
        slot.setDisplay(display);
        return slot.copy(false);
    }

    private String actionID;
    private SlotDisplayValues display;

    public ActionSlotValues(boolean mutable) {
        super(mutable);
        this.actionID = "";
        this.display = null;
    }

    public ActionSlotValues(ActionSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.actionID = toCopy.getActionID();
        this.display = toCopy.getDisplay();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ACTION);
        output.addByte((byte) 1);

        output.addString(actionID);
        output.addBoolean(display != null);
        if (display != null) display.save(output);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ActionSlotValues) {
            ActionSlotValues otherSlot = (ActionSlotValues) other;
            return this.actionID.equals(otherSlot.actionID) && Objects.equals(this.display, otherSlot.display);
        } else return false;
    }

    @Override
    public ActionSlotValues copy(boolean mutable) {
        return new ActionSlotValues(this, mutable);
    }

    @Override
    public ActionSlotValues nonConflictingCopy(CustomContainerValues container) {
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

    public String getActionID() {
        return actionID;
    }

    public SlotDisplayValues getDisplay() {
        return display;
    }

    public void setActionID(String actionID) {
        assertMutable();
        this.actionID = Objects.requireNonNull(actionID);
    }

    public void setDisplay(SlotDisplayValues display) {
        assertMutable();
        this.display = display != null ? display.copy(false) : null;
    }

    @Override
    public void validate(ItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (actionID == null) throw new ProgrammingValidationException("No actionID");
        if (actionID.isEmpty()) throw new ValidationException("You need to insert an action ID");
        if (display != null) Validation.scope("Display", display::validate, itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (display != null) Validation.scope("Display", display::validateExportVersion, version);
    }
}
