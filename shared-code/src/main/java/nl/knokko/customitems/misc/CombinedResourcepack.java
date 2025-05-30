package nl.knokko.customitems.misc;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipInputStream;

public class CombinedResourcepack extends ModelValues {

    public static CombinedResourcepack load(BitInput input, ItemSet.Side side) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 3) throw new UnknownEncodingException("CombinedResourcepack", encoding);

        CombinedResourcepack combinedPack = new CombinedResourcepack(false);
        combinedPack.name = input.readString();
        combinedPack.priority = input.readInt();
        if (encoding > 1) combinedPack.isGeyser = input.readBoolean();
        else combinedPack.isGeyser = false;

        if (encoding < 3 || side == ItemSet.Side.EDITOR) {
            combinedPack.content = input.readByteArray();
        } else combinedPack.content = null;
        return combinedPack;
    }

    private String name;
    private int priority;
    private boolean isGeyser;
    private byte[] content;

    public CombinedResourcepack(boolean mutable) {
        super(mutable);
        this.name = "";
        this.priority = -1;
        this.isGeyser = false;
        this.content = null;
    }

    public CombinedResourcepack(CombinedResourcepack toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.priority = toCopy.getPriority();
        this.isGeyser = toCopy.isGeyser();
        this.content = toCopy.getContent();
    }

    public void save(BitOutput output, ItemSet.Side targetSide) {
        output.addByte((byte) 3);
        output.addString(name);
        output.addInt(priority);
        output.addBoolean(isGeyser);
        if (targetSide == ItemSet.Side.EDITOR) output.addByteArray(content);
    }

    @Override
    public CombinedResourcepack copy(boolean mutable) {
        return new CombinedResourcepack(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CombinedResourcepack) {
            CombinedResourcepack otherPack = (CombinedResourcepack) other;
            return this.name.equals(otherPack.name) && this.priority == otherPack.priority
                    && this.isGeyser == otherPack.isGeyser && Arrays.equals(this.content, otherPack.content);
        } else return false;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isGeyser() {
        return isGeyser;
    }

    public byte[] getContent() {
        return content;
    }

    public void setName(String name) {
        assertMutable();
        this.name = Objects.requireNonNull(name);
    }

    public void setPriority(int priority) {
        assertMutable();
        this.priority = priority;
    }

    public void setGeyser(boolean geyser) {
        assertMutable();
        this.isGeyser = geyser;
    }

    public void setContent(byte[] content) {
        assertMutable();
        this.content = Objects.requireNonNull(content);
    }

    public void validate(ItemSet itemSet, String oldName, Integer oldPriority) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        if (priority == 0) throw new ValidationException("Priority can't be 0");
        if (content == null) throw new ValidationException("You need to pick a file");

        try {
            ZipInputStream dummyInput = new ZipInputStream(new ByteArrayInputStream(content));
            boolean hasEntry = false;
            while (dummyInput.getNextEntry() != null) {
                hasEntry = true;
                dummyInput.closeEntry();
            }
            dummyInput.close();

            if (!hasEntry) throw new ValidationException("The ZIP file is empty");
        } catch (IOException invalidZip) {
            throw new ValidationException("Invalid ZIP file: " + invalidZip.getLocalizedMessage());
        }

        boolean changedName = oldName == null || !oldName.equals(this.name);
        if (changedName && itemSet.combinedResourcepacks.stream().anyMatch(
                otherPack -> otherPack.isGeyser() == this.isGeyser && otherPack.getName().equals(this.name)
        )) {
            throw new ValidationException("Another pack already has this name");
        }

        boolean changedPriority = oldPriority == null || oldPriority != this.priority;
        if (changedPriority && itemSet.combinedResourcepacks.stream().anyMatch(
                otherPack -> otherPack.isGeyser() == this.isGeyser && otherPack.getPriority() == this.priority
        )) {
            throw new ValidationException("Another pack already has this priority");
        }
    }
}
