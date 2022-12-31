package nl.knokko.customitems.util;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CollectionHelper {

    public static <T, R> Optional<T> find(Collection<T> collection, Function<T, R> extract, R value) {
        return collection.stream().filter(candidate -> extract.apply(candidate).equals(value)).findFirst();
    }

    public static byte[] arrayCopy(byte[] original) {
        if (original == null) return null;
        return Arrays.copyOf(original, original.length);
    }

    public static <T> void save(Collection<T> collection, Consumer<T> save, BitOutput output) {
        output.addInt(collection.size());
        for (T element : collection) save.accept(element);
    }

    public static <T> List<T> load(BitInput input, LoadFunction<T> load) throws UnknownEncodingException {
        int size = input.readInt();
        List<T> collection = new ArrayList<>(size);

        for (int counter = 0; counter < size; counter++) collection.add(load.load(input));
        return collection;
    }

    public static <T> List<T> load(BitInput input, LoadFunction2<T> load) throws UnknownEncodingException {
        return load(input, input1 -> load.load(input1, false));
    }

    @FunctionalInterface
    public interface LoadFunction<T> {
        T load(BitInput input) throws UnknownEncodingException;
    }

    @FunctionalInterface
    public interface LoadFunction2<T> {
        T load(BitInput input, boolean mutable) throws UnknownEncodingException;
    }
}
