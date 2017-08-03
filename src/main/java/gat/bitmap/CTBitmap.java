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

    //Name declared for the Plugin
    public static final String NAME = "BitMap";

    //Name Of the attribute
    private static final String BITS = "bits";
    private static final int BITS_H = HashHelper.hash(BITS);


    private RoaringBitmap bitmap;
    private EStruct root;

    /**
     * Constructor that will look for a root node, if not existing will create a new one.
     * Then will look for the serialized form of the bitmap in the BITS attribute and deserialize it, if none is existing
     * then a new BitMap is created.
     * @param backend
     */
    public CTBitmap(EStructArray backend) {
        super(backend);
        root = backend.root();
        if (root == null) {
            root = backend.newEStruct();
            backend.setRoot(root);
        }
        String gBits = (String) root.getAt(BITS_H);
        if (gBits != null) {
            ByteBuffer newbb = ByteBuffer.wrap(Base64.getDecoder().decode(gBits));
            bitmap = new ImmutableRoaringBitmap(newbb).toRoaringBitmap();
        } else {
            bitmap = new RoaringBitmap();
        }
    }

    /**
     * save the current bitmap by serializing it and store it in the BITS attribute of the root node.
     * Must be called by the user whenever done with the modification of the bitmap
     */
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

    /**
     * getter for the bitmap
     * @return the bitmap
     */
    public RoaringBitmap getBitMap() {
        return bitmap;
    }

    /**
     * Setter for the bitmap, to use in case a new bitmap was created and should be saved instead of the former one.
     * @param bitmap
     */
    public void setBitmap(RoaringBitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Clear the bitmap
     */
    public void clear() {
        root.setAt(BITS_H, Type.STRING, "");
        bitmap.clear();
    }

    /**
     * Set the bit at index to one
     * @param index
     * @return true if done false if already set
     */
    public boolean set(int index) {
        return bitmap.checkedAdd(index);
    }

    /**
     * Set all bit present in the array indexs to one
     * @param indexs
     */
    public void setAll(int... indexs) {
        bitmap.add(indexs);
    }

    /**
     * Unset the bit at the given index
     * @param index
     * @return true if done false if already unset
     */
    public boolean unset(int index) {
        return bitmap.checkedRemove(index);
    }

    /**
     * @return the size of the bitmap
     */
    public int size() {
        return bitmap.last() + 1;
    }

    /**
     * @return the cardinality, i.e., the number of bit set, in the bitmap
     */
    public int cardinality() {
        return bitmap.getCardinality();
    }

    /**
     * determine whether the bit at index is set or not
     * @param index
     * @return
     */
    public boolean get(int index) {
        return bitmap.contains(index);
    }

    /**
     * determine the next set bit after startIndex
     * @param startIndex
     * @return
     */
    public int nextSetBit(int startIndex) {
        PeekableIntIterator iterator = bitmap.getIntIterator();
        iterator.advanceIfNeeded(startIndex);
        return iterator.peekNext();
    }

    /**
     * Iterator over the set bits
     * @return
     */
    public IntIterator iterator() {
        return bitmap.getIntIterator();
    }
}
