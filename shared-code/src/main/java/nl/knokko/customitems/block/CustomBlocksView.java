package nl.knokko.customitems.block;

import java.util.Collection;
import java.util.Iterator;

public class CustomBlocksView implements Iterable<CustomBlockView> {

    private final Collection<CustomBlock> liveBlockList;

    public CustomBlocksView(Collection<CustomBlock> liveBlockList) {
        this.liveBlockList = liveBlockList;
    }

    @Override
    public Iterator<CustomBlockView> iterator() {
        return new CustomBlockViewIterator(liveBlockList.iterator());
    }

    public int size() {
        return liveBlockList.size();
    }

    private static class CustomBlockViewIterator implements Iterator<CustomBlockView> {

        private final Iterator<CustomBlock> blockIterator;

        CustomBlockViewIterator(Iterator<CustomBlock> blockIterator) {
            this.blockIterator = blockIterator;
        }

        @Override
        public boolean hasNext() {
            return blockIterator.hasNext();
        }

        @Override
        public CustomBlockView next() {
            return new CustomBlockView(blockIterator.next());
        }
    }
}
