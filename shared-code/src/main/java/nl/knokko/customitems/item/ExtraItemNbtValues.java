package nl.knokko.customitems.item;

import nl.knokko.customitems.item.nbt.NbtValueType;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtraItemNbtValues extends ModelValues {

    private static final byte ENCODING_1 = 1;

    public static ExtraItemNbtValues load(BitInput input, boolean mutable) throws UnknownEncodingException {
        byte encoding = input.readByte();
        ExtraItemNbtValues nbt = new ExtraItemNbtValues(mutable);
        if (encoding == ENCODING_1) {
            nbt.load1(input);
        } else {
            throw new UnknownEncodingException("ExtraItemNbt", encoding);
        }
        return nbt;
    }

    public static ExtraItemNbtValues createQuick(Collection<Entry> entries) {
        ExtraItemNbtValues result = new ExtraItemNbtValues(true);
        result.setEntries(entries);
        return result;
    }

    private Collection<Entry> entries;

    public ExtraItemNbtValues(boolean mutable) {
        super(mutable);

        this.entries = new ArrayList<>(0);
    }

    public ExtraItemNbtValues(ExtraItemNbtValues toCopy, boolean mutable) {
        super(mutable);

        this.entries = toCopy.getEntries();
    }

    private void load1(BitInput input) throws UnknownEncodingException {
        int numEntries = input.readInt();
        this.entries = new ArrayList<>(numEntries);
        for (int counter = 0; counter < numEntries; counter++) {
            this.entries.add(Entry.load1(input, false));
        }
    }

    @Override
    public String toString() {
        return "ExtraItemNBT(" + entries + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ExtraItemNbtValues) {
            return this.entries.equals(((ExtraItemNbtValues) other).entries);
        } else {
            return false;
        }
    }

    @Override
    public ExtraItemNbtValues copy(boolean mutable) {
        return new ExtraItemNbtValues(this, mutable);
    }

    public void save(BitOutput output) {
        output.addByte(ENCODING_1);
        save1(output);
    }

    private void save1(BitOutput output) {
        output.addInt(entries.size());
        for (Entry entry : entries) {
            entry.save1(output);
        }
    }

    public Collection<Entry> getEntries() {
        return new ArrayList<>(entries);
    }

    public void setEntries(Collection<Entry> newEntries) {
        assertMutable();
        Checks.nonNull(newEntries);
        this.entries = Mutability.createDeepCopy(newEntries, false);
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (entries == null) throw new ProgrammingValidationException("No entries");
        for (Entry entry : entries) {
            entry.validate();
        }
    }

    public static class Entry extends ModelValues {

        public static Entry load1(BitInput input, boolean mutable) throws UnknownEncodingException {
            Entry result = new Entry(mutable);
            result.load1(input);
            return result;
        }

        public static Entry createQuick(List<String> key, Value value) {
            Entry result = new Entry(true);
            result.setKey(key);
            result.setValue(value);
            return result;
        }

        private List<String> key;
        private Value value;

        public Entry(boolean mutable) {
            super(mutable);

            this.key = new ArrayList<>(1);
            this.key.add("");

            this.value = new Value("");
        }

        public Entry(Entry toCopy, boolean mutable) {
            super(mutable);

            this.key = toCopy.getKey();
            this.value = toCopy.getValue();
        }

        private void load1(BitInput input) throws UnknownEncodingException {
            loadKey1(input);
            this.value = Value.load1(input);
        }

        private void loadKey1(BitInput input) {
            int numKeyParts = input.readInt();
            this.key = new ArrayList<>(numKeyParts);
            for (int counter = 0; counter < numKeyParts; counter++) {
                this.key.add(input.readString());
            }
        }

        @Override
        public String toString() {
            return "Entry(" + key + " = " + value + ")";
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Entry) {
                Entry otherEntry = (Entry) other;
                return this.key.equals(otherEntry.key) && this.value.equals(otherEntry.value);
            } else {
                return false;
            }
        }

        @Override
        public ModelValues copy(boolean mutable) {
            return new Entry(this, mutable);
        }

        private void save1(BitOutput output) {
            saveKey1(output);
            value.save1(output);
        }

        private void saveKey1(BitOutput output) {
            output.addInt(key.size());
            for (String keyPart : key) {
                output.addString(keyPart);
            }
        }

        public List<String> getKey() {
            return new ArrayList<>(key);
        }

        public Value getValue() {
            return value;
        }

        public void setKey(List<String> newKey) {
            assertMutable();
            Checks.nonNull(newKey);
            this.key = new ArrayList<>(newKey);
        }

        public void setValue(Value newValue) {
            assertMutable();
            this.value = newValue;
        }

        public void validate() throws ValidationException, ProgrammingValidationException {
            if (key == null) throw new ProgrammingValidationException("No key");
            if (key.isEmpty()) throw new ProgrammingValidationException("Empty key");
            for (String keyPart : key) {
                if (keyPart.isEmpty()) {
                    throw new ValidationException("A part of the key is empty");
                }
            }
            if (value == null) throw new ProgrammingValidationException("No value");
        }
    }

    public static class Value {

        private static Value load1(BitInput input) throws UnknownEncodingException {
            byte typeOrdinal = input.readByte();
            if (typeOrdinal >= NbtValueType.values().length) {
                throw new UnknownEncodingException("NbtValueType", typeOrdinal);
            }
            NbtValueType type = NbtValueType.values()[typeOrdinal];
            if (type == NbtValueType.STRING) {
                return new Value(input.readString());
            } else if (type == NbtValueType.INTEGER) {
                return new Value(input.readInt());
            } else {
                throw new Error("Forgot NbtValueType " + type);
            }
        }

        public final NbtValueType type;
        public final Object value;

        public Value(Object value) {
            if (value instanceof String) {
                this.type = NbtValueType.STRING;
            } else if (value instanceof Integer) {
                this.type = NbtValueType.INTEGER;
            } else {
                throw new IllegalStateException("Unsupported value class: " + value.getClass());
            }
            this.value = value;
        }

        public int getIntValue() {
            if (type != NbtValueType.INTEGER) throw new UnsupportedOperationException("Type is not Int");
            return (Integer) value;
        }

        public String getStringValue() {
            if (type != NbtValueType.STRING) throw new UnsupportedOperationException("Type is not String");
            return (String) value;
        }

        private void save1(BitOutput output) {
            output.addByte((byte) type.ordinal());
            if (type == NbtValueType.INTEGER) {
                output.addInt((Integer) value);
            } else if (type == NbtValueType.STRING) {
                output.addString((String) value);
            } else {
                throw new Error("Forgot NbtValueType " + type);
            }
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Value) {
                return this.value.equals(((Value) other).value);
            } else {
                return false;
            }
        }
    }
}
