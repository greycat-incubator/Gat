package gat.bitset;

import greycat.Type;
import greycat.base.BaseCustomType;
import greycat.struct.EStruct;
import greycat.struct.EStructArray;
import greycat.struct.IntArray;
import greycat.utility.HashHelper;

import java.util.List;

public class CTBitset extends BaseCustomType {
    public static final String BITS = "bits";
    protected static final int BITS_H = HashHelper.hash(BITS);
    public static final String NAME = "BitSet";

    private IntArray gBits;
    private BitSet bitset;
    private EStruct root;

    public CTBitset(EStructArray p_backend) {
        super(p_backend);
        root = p_backend.root();
        if (root == null) {
            root = p_backend.newEStruct();
            p_backend.setRoot(root);
        }
        gBits = (IntArray) root.getOrCreateAt(BITS_H, Type.INT_ARRAY);
        bitset = new BitSet(gBits.extract());
    }

    public void clear() {
        gBits.initWith(new int[0]);
        bitset.clear();
    }

    public boolean add(int index) {
        return bitset.add(index);
    }

    public void clear(int index) {
        bitset.clear(index);
    }

    public int size() {
        return bitset.size();
    }

    public int cardinality() {
        return bitset.cardinality();
    }

    public boolean get(int index) {
        return bitset.get(index);
    }

    public int nextSetBit(int startIndex) {
        return bitset.nextSetBit(startIndex);
    }

    public BitSetIterator iterator() { return bitset.iterator();}

    public void save() {
        gBits.initWith(bitset.toIntArray());
    }

    public BitSet getBitset() {
        return bitset;
    }

    public void setBitset(BitSet bitSet) {
        this.bitset = bitSet;
    }

}
