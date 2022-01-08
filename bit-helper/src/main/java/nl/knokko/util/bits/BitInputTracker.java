package nl.knokko.util.bits;

public class BitInputTracker extends BitInput {

    private final BitInput input;
    private final ByteArrayBitOutput readBits;

    public BitInputTracker(BitInput input, int expectedNumBytesToRead) {
        this.input = input;
        this.readBits = new ByteArrayBitOutput(expectedNumBytesToRead);
    }

    @Override
    public boolean readDirectBoolean() {
        boolean result = input.readDirectBoolean();
        readBits.addDirectBoolean(result);
        return result;
    }

    @Override
    public byte readDirectByte() {
        byte result = input.readDirectByte();
        readBits.addDirectByte(result);
        return result;
    }

    @Override
    public void increaseCapacity(int booleans) {
        input.increaseCapacity(booleans);
        readBits.ensureExtraCapacity(booleans);
    }

    @Override
    public void terminate() {
        input.terminate();
        readBits.terminate();
    }

    public byte[] getReadBytes() {
        return readBits.getBytes();
    }
}
