package gat.bitmap;

import greycat.Type;
import greycat.base.BaseCustomType;
import greycat.struct.EStruct;
import greycat.struct.EStructArray;
import greycat.utility.HashHelper;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.PeekableIntIterator;
import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;

public class CTBitmap extends BaseCustomType {
    private static final String BITS = "bits";
    private static final int BITS_H = HashHelper.hash(BITS);
    public static final String NAME = "BitMap";
    private String gBits;
    private RoaringBitmap bitmap;
    private EStruct root;

    public CTBitmap(EStructArray backend) {
        super(backend);
        root = backend.root();
        if (root == null) {
            root = backend.newEStruct();
            backend.setRoot(root);
        }
        gBits = (String) root.getAt(BITS_H);
        if (gBits != null) {
            ByteBuffer newbb = ByteBuffer.wrap(Base64.getDecoder().decode(gBits));
            bitmap = new ImmutableRoaringBitmap(newbb).toRoaringBitmap();
        } else {
            bitmap = new RoaringBitmap();
        }
    }

    public void save() {
        bitmap.runOptimize();
        ByteBuffer outbb = ByteBuffer.allocate(bitmap.serializedSizeInBytes());
        try {
            bitmap.serialize(new DataOutputStream(new OutputStream() {
                ByteBuffer mBB;

                OutputStream init(ByteBuffer mbb) {
                    mBB = mbb;
                    return this;
                }

                public void close() {
                }

                public void flush() {
                }

                public void write(int b) {
                    mBB.put((byte) b);
                }

                public void write(byte[] b) {
                    mBB.put(b);
                }

                public void write(byte[] b, int off, int l) {
                    mBB.put(b, off, l);
                }
            }.init(outbb)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
        outbb.flip();
        root.setAt(BITS_H, Type.STRING, Base64.getEncoder().encodeToString(outbb.array()));

    }

    public RoaringBitmap getBitMap() {
        return bitmap;
    }

    public void setBitmap(RoaringBitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void clear() {
        root.setAt(BITS_H, Type.STRING, "");
        bitmap.clear();
    }


    public boolean add(int index) {
        return bitmap.checkedAdd(index);
    }


    public boolean addAll(List<Integer> indexs) {
        bitmap.add(indexs.stream().mapToInt(i -> i).toArray());
        return true;
    }


    public void clear(int index) {
        bitmap.checkedRemove(index);
    }


    public int size() {
        return bitmap.last() + 1;
    }


    public int cardinality() {
        return bitmap.getCardinality();
    }

    public boolean get(int index) {
        return bitmap.contains(index);
    }

    public int nextSetBit(int startIndex) {
        PeekableIntIterator iterator = bitmap.getIntIterator();
        iterator.advanceIfNeeded(startIndex);
        return iterator.peekNext();
    }

    public IntIterator iterator() {
        return bitmap.getIntIterator();
    }
}
