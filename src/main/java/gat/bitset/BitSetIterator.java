package gat.bitset;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over a bit set.
 */
public final class BitSetIterator implements Iterator<Integer> {

    private BitSet bitset;
    private int nextIndex;
    private int currentIndex = -1;
    private boolean reversed;

    public BitSetIterator(BitSet that, int from, boolean reversed) {
        bitset = that;
        this.nextIndex = reversed ? that.previousSetBit(from) : that.nextSetBit(from);
        this.reversed = reversed;
    }

    public boolean hasNext() {
        return (nextIndex >= 0);
    }

    public Integer next() {
        if (nextIndex < 0)
            throw new NoSuchElementException();
        currentIndex = nextIndex;
        nextIndex = reversed ? bitset.previousSetBit(nextIndex - 1) : bitset.nextSetBit(nextIndex + 1);
        return currentIndex;
    }

    public void remove() {
        if (currentIndex < 0)
            throw new IllegalStateException();
        bitset.clear(currentIndex);
        currentIndex = -1;
    }

}