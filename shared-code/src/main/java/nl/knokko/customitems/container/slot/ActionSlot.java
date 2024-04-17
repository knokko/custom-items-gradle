package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Collection;
import java.util.Objects;

import static nl.knokko.customitems.container.slot.ContainerSlot.Encodings.ACTION;

public class ActionSlot extends ContainerSlot {

    static ActionSlot loadAction(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ActionSlot", encoding);

        ActionSlot slot = new ActionSlot(false);
        slot.actionID = input.readString();
        if (input.readBoolean()) slot.display = SlotDisplay.load(input, itemSet);
        else slot.display = null;
        return slot;
    }

    public static ActionSlot createQuick(String actionID, SlotDisplay display) {
        ActionSlot slot = new ActionSlot(true);
        slot.setActionID(actionID);
        slot.setDisplay(display);
        return slot.copy(false);
    }

    private String actionID;
    private SlotDisplay display;

    public ActionSlot(boolean mutable) {
        super(mutable);
        this.actionID = "";
        this.display = null;
    }

    public ActionSlot(ActionSlot toCopy, boolean mutable) {
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
        if (other instanceof ActionSlot) {
            ActionSlot otherSlot = (ActionSlot) other;
            return this.actionID.equals(otherSlot.actionID) && Objects.equals(this.display, otherSlot.display);
        } else return false;
    }

    @Override
    public ActionSlot copy(boolean mutable) {
        return new ActionSlot(this, mutable);
    }

    @Override
    public ActionSlot nonConflictingCopy(KciContainer container) {
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

    public SlotDisplay getDisplay() {
        return display;
    }

    public void setActionID(String actionID) {
        assertMutable();
        this.actionID = Objects.requireNonNull(actionID);
    }

    public void setDisplay(SlotDisplay display) {
        assertMutable();
        this.display = display != null ? display.copy(false) : null;
    }

    @Override
    public void validate(ItemSet itemSet, Collection<ContainerSlot> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (actionID == null) throw new ProgrammingValidationException("No actionID");
        if (actionID.isEmpty()) throw new ValidationException("You need to insert an action ID");
        if (display != null) Validation.scope("Display", display::validate, itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (display != null) Validation.scope("Display", display::validateExportVersion, version);
    }
}
