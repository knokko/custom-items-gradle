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

public class CombinedResourcepackValues extends ModelValues {

    public static CombinedResourcepackValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CombinedResourcepack", encoding);

        CombinedResourcepackValues combinedPack = new CombinedResourcepackValues(false);
        combinedPack.name = input.readString();
        combinedPack.priority = input.readInt();
        combinedPack.content = input.readByteArray();
        return combinedPack;
    }

    private String name;
    private int priority;
    private byte[] content;

    public CombinedResourcepackValues(boolean mutable) {
        super(mutable);
        this.name = "";
        this.priority = -1;
        this.content = null;
    }

    public CombinedResourcepackValues(CombinedResourcepackValues toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.priority = toCopy.getPriority();
        this.content = toCopy.getContent();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);
        output.addString(name);
        output.addInt(priority);
        output.addByteArray(content);
    }

    @Override
    public CombinedResourcepackValues copy(boolean mutable) {
        return new CombinedResourcepackValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CombinedResourcepackValues) {
            CombinedResourcepackValues otherPack = (CombinedResourcepackValues) other;
            return this.name.equals(otherPack.name) && this.priority == otherPack.priority
                    && Arrays.equals(this.content, otherPack.content);
        } else return false;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
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
        if (changedName && itemSet.combinedResourcepacks.stream().anyMatch(otherPack -> otherPack.getName().equals(this.name))) {
            throw new ValidationException("Another pack already has this name");
        }

        boolean changedPriority = oldPriority == null || oldPriority != this.priority;
        if (changedPriority && itemSet.combinedResourcepacks.stream().anyMatch(otherPack -> otherPack.getPriority() == this.priority)) {
            throw new ValidationException("Another pack already has this priority");
        }
    }
}
