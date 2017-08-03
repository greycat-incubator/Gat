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

import greycat.Type;
import greycat.base.BaseCustomType;
import greycat.struct.EStruct;
import greycat.struct.EStructArray;
import greycat.struct.IntArray;
import greycat.utility.HashHelper;

public class CTBitset extends BaseCustomType {

    //Name declared for the Plugin
    public static final String NAME = "BitSet";

    //Name Of the attribute
    private static final String BITS = "bits";
    private static final int BITS_H = HashHelper.hash(BITS);

    private IntArray gBits;
    private BitSet bitset;

    /**
     * Constructor that will look for a root node, if not existing will create a new one.
     * Then will look for the bitset in the BITS attribute and load it, if none is existing
     * then a new Bitset is created.
     *
     * @param p_backend
     */
    public CTBitset(EStructArray p_backend) {
        super(p_backend);
        EStruct root = p_backend.root();
        if (root == null) {
            root = p_backend.newEStruct();
            p_backend.setRoot(root);
        }
        gBits = (IntArray) root.getOrCreateAt(BITS_H, Type.INT_ARRAY);
        bitset = new BitSet(gBits.extract());
    }

    /**
     * Clear the bitset
     */
    public void clear() {
        gBits.initWith(new int[0]);
        bitset.clear();
    }

    /**
     * Set the bit at index to one
     *
     * @param index
     * @return true if done false if already set
     */
    public boolean set(int index) {
        return bitset.add(index);
    }

    /**
     * Unset the bit at the given index
     *
     * @param index
     * @return true if done false if already unset
     */
    public void unset(int index) {
        bitset.clear(index);
    }

    /**
     * @return the size of the bitset
     */
    public int size() {
        return bitset.size();
    }

    /**
     * @return the cardinality, i.e., the number of bit set, in the bitset
     */
    public int cardinality() {
        return bitset.cardinality();
    }

    /**
     * determine whether the bit at index is set or not
     *
     * @param index
     * @return
     */
    public boolean get(int index) {
        return bitset.get(index);
    }

    /**
     * determine the next set bit after startIndex
     *
     * @param startIndex
     * @return
     */
    public int nextSetBit(int startIndex) {
        return bitset.nextSetBit(startIndex);
    }

    /**
     * Iterator over the set bits
     *
     * @return
     */
    public BitSetIterator iterator() {
        return bitset.iterator();
    }

    /**
     * save the current bitset in the BITS attribute of the root node.
     * Must be called by the user whenever done with the modification of the bitset
     */
    public void save() {
        gBits.initWith(bitset.toIntArray());
    }

    /**
     * getter for the bitset
     *
     * @return the bitmap
     */
    public BitSet getBitset() {
        return bitset;
    }

    /**
     * Setter for the bitset, to use in case a new bitset was created and should be saved instead of the former one.
     *
     * @param bitSet
     */
    public void setBitset(BitSet bitSet) {
        this.bitset = bitSet;
    }

}
