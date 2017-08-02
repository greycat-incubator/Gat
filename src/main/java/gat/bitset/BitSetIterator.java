/**
 * Copyright 2017 the Greycat Additional Types authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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