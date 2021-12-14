package nl.knokko.customitems.model;

import nl.knokko.customitems.util.Checks;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class CollectionView<M extends Model<V>, V extends ModelValues, R extends Supplier<V>> implements Iterable<V> {

    private final Collection<M> liveCollection;
    private final Function<M, R> createReference;

    public CollectionView(Collection<M> liveCollection, Function<M, R> createReference) {
        Checks.notNull(liveCollection);
        this.liveCollection = liveCollection;
        this.createReference = createReference;
    }

    @Override
    public Iterator<V> iterator() {
        return new CollectionViewIterator<>(liveCollection.iterator());
    }

    public int size() {
        return liveCollection.size();
    }

    public Stream<V> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public Iterable<R> references() {
        return new References();
    }

    private static class CollectionViewIterator<M extends Model<V>, V extends ModelValues> implements Iterator<V> {

        private final Iterator<M> modelIterator;

        CollectionViewIterator(Iterator<M> modelIterator) {
            this.modelIterator = modelIterator;
        }

        @Override
        public boolean hasNext() {
            return modelIterator.hasNext();
        }

        @Override
        public V next() {
            return modelIterator.next().getValues();
        }
    }

    private class References implements Iterable<R> {

        @Override
        public Iterator<R> iterator() {
            return new ReferenceIterator(liveCollection.iterator());
        }
    }

    private class ReferenceIterator implements Iterator<R> {

        private final Iterator<M> modelIterator;

        private ReferenceIterator(Iterator<M> modelIterator) {
            this.modelIterator = modelIterator;
        }

        @Override
        public boolean hasNext() {
            return modelIterator.hasNext();
        }

        @Override
        public R next() {
            return createReference.apply(modelIterator.next());
        }
    }
}
