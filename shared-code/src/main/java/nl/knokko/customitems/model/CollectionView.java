package nl.knokko.customitems.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class CollectionView<M extends Model<V>, V extends ModelValues> implements Iterable<V> {

    private final Collection<M> liveCollection;

    public CollectionView(Collection<M> liveCollection) {
        this.liveCollection = liveCollection;
    }

    @Override
    public Iterator<V> iterator() {
        return new CollectionViewIterator<M, V>(liveCollection.iterator());
    }

    public int size() {
        return liveCollection.size();
    }

    public Stream<V> stream() {
        return StreamSupport.stream(this.spliterator(), false);
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
}
