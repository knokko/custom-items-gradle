package nl.knokko.customitems.editor.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleSupplier<T> implements Supplier<T> {

    private static class SimpleIterator<T> implements Iterator<SimpleSupplier<T>> {

        private Iterator<T> iterator;

        SimpleIterator(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public SimpleSupplier<T> next() {
            return new SimpleSupplier<>(iterator.next());
        }
    }

    public static <T> Iterable<SimpleSupplier<T>> createIterable(Collection<T> collection) {
        return () -> new SimpleIterator<>(collection.iterator());
    }

    public static <T> Consumer<T> changeInCollection(T toReplace, Collection<SimpleSupplier<T>> backingCollection) {
        return newValues -> {
            int index = 0;
            Iterator<SimpleSupplier<T>> iterator = backingCollection.iterator();
            while (iterator.hasNext()) {

                SimpleSupplier<T> candidate = iterator.next();
                if (candidate.get() == toReplace) {
                    iterator.remove();
                    break;
                }

                index++;
            }

            if (index == backingCollection.size()) throw new IllegalArgumentException("toReplace is not in backingCollection");

            if (backingCollection instanceof List) {
                List<SimpleSupplier<T>> backingList = (List<SimpleSupplier<T>>) backingCollection;
                backingList.add(index, new SimpleSupplier<>(newValues));
            } else {
                backingCollection.add(new SimpleSupplier<>(newValues));
            }
        };
    }

    private final T value;

    public SimpleSupplier(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
