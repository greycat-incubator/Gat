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
package gat.set;

import greycat.Constants;
import greycat.Type;
import greycat.base.BaseCustomType;
import greycat.struct.EStruct;
import greycat.struct.EStructArray;
import greycat.struct.IntArray;
import greycat.utility.HashHelper;

import java.util.Arrays;

public class CTIntSet extends BaseCustomType {

    private final IntArray keys;
    private EStruct root;
    private int mapSize = 0;
    private int capacity = 0;

    private static final String KEYS = "keys";
    private static final int KEYS_H = HashHelper.hash(KEYS);
    public static final String NAME = "IntSet";

    private int[] nexts = null;
    private int[] hashs = null;

    /**
     * Constructor that will look for a root node, if not existing will create a new one.
     * Then will look for the int set in the KEYS attribute and load it, if none is existing
     * then a new set is created.
     *
     * @param p_backend
     */
    public CTIntSet(EStructArray p_backend) {
        super(p_backend);
        root = p_backend.root();
        if (root == null) {
            root = p_backend.newEStruct();
            p_backend.setRoot(root);
        }
        keys = (IntArray) root.getOrCreateAt(KEYS_H, Type.INT_ARRAY);
        for (int i = 0; i < keys.size(); i++) {
            put(keys.get(i));
        }

    }

    private int key(int i) {
        return keys.get(i);
    }

    private void addKey(int newValue) {
        keys.addElement(newValue);
    }

    private void setKey(int i, int newValue) {
        keys.set(i, newValue);
    }

    private int next(int i) {
        return nexts[i];
    }

    private void setNext(int i, int newValue) {
        nexts[i] = newValue;
    }

    private int hash(int i) {
        return hashs[i];
    }

    private void setHash(int i, int newValue) {
        hashs[i] = newValue;
    }


    void reallocate(int newCapacity) {
        if (newCapacity > capacity) {
            //extend keys
            int[] new_nexts = new int[newCapacity];
            int[] new_hashes = new int[newCapacity * 2];
            Arrays.fill(new_nexts, 0, newCapacity, -1);
            Arrays.fill(new_hashes, 0, (newCapacity * 2), -1);
            hashs = new_hashes;
            nexts = new_nexts;
            for (int i = 0; i < mapSize; i++) {
                int new_key_hash = (int) HashHelper.longHash(key(i), newCapacity * 2);
                setNext(i, hash(new_key_hash));
                setHash(new_key_hash, i);
            }
            capacity = newCapacity;
        }
    }

    /**
     * Insert a new key in the set
     * @return true if inserted, false if already existing
     */
    public boolean put(int insertKey) {
        boolean result = false;

        if (hashs == null) {
            reallocate(Constants.MAP_INITIAL_CAPACITY);
            addKey(insertKey);
            setHash(HashHelper.intHash(insertKey, capacity * 2), 0);
            setNext(0, -1);
            mapSize++;
        } else {
            int hashCapacity = capacity * 2;
            int insertKeyHash = HashHelper.intHash(insertKey, hashCapacity);
            int currentHash = hash(insertKeyHash);
            int m = currentHash;
            int found = -1;
            while (m >= 0) {
                if (insertKey == key(m)) {
                    found = m;
                    break;
                }
                m = next(m);
            }
            if (found == -1) {
                result = true;
                final int lastIndex = mapSize;
                if (lastIndex == capacity) {
                    reallocate(capacity * 2);
                    hashCapacity = capacity * 2;
                    insertKeyHash = HashHelper.intHash(insertKey, hashCapacity);
                    currentHash = hash(insertKeyHash);
                }
                addKey(insertKey);
                setHash(HashHelper.intHash(insertKey, capacity * 2), lastIndex);
                setNext(lastIndex, currentHash);
                mapSize++;
            }

        }
        return result;
    }

    /**
     * Check whether a key is present in the set
     * @param requestKey
     * @return true if yes false otherwise
     */
    public boolean contains(int requestKey) {
        boolean result = false;
        if (hashs != null) {
            final int hashIndex = HashHelper.intHash(requestKey, capacity * 2);
            int m = hash(hashIndex);
            while (m >= 0) {
                if (requestKey == key(m)) {
                    result = true;
                    break;
                }
                m = next(m);
            }
        }
        return result;
    }

    /**
     * Check whether a key is present in the set
     * @param requestKey
     * @return the position of the key is present  -1 otherwise
     */
    public int index(int requestKey) {
        int result = -1;
        if (hashs != null) {
            final int hashIndex = HashHelper.intHash(requestKey, capacity * 2);
            int m = hash(hashIndex);
            while (m >= 0) {
                if (requestKey == key(m)) {
                    result = m;
                    break;
                }
                m = next(m);
            }
        }
        return result;
    }

    /**
     * Remove a key from the set
     * @param requestKey
     * @return true if key was removed, false if key was absent from the set
     */
    public boolean remove(int requestKey) {
        boolean result = false;
        if (hashs != null && mapSize != 0) {
            int hashCapacity = capacity * 2;
            int hashIndex = HashHelper.intHash(requestKey, hashCapacity);
            int m = hash(hashIndex);
            int found = -1;
            while (m >= 0) {
                if (requestKey == key(m)) {
                    found = m;
                    break;
                }
                m = next(m);
            }
            if (found != -1) {
                result = true;
                //first remove currentKey from hashChain
                int toRemoveHash = HashHelper.intHash(requestKey, hashCapacity);
                m = hash(toRemoveHash);
                if (m == found) {
                    setHash(toRemoveHash, next(m));
                } else {
                    while (m != -1) {
                        int next_of_m = next(m);
                        if (next_of_m == found) {
                            setNext(m, next(next_of_m));
                            break;
                        }
                        m = next_of_m;
                    }
                }
                final int lastIndex = mapSize - 1;
                if (lastIndex == found) {
                    //easy, was the last element
                    mapSize--;
                } else {
                    //less cool, we have to unchain the last value of the map
                    final int lastKey = key(lastIndex);
                    setKey(found, lastKey);
                    setNext(found, next(lastIndex));
                    int victimHash = HashHelper.intHash(lastKey, hashCapacity);
                    m = hash(victimHash);
                    if (m == lastIndex) {
                        //the victim was the head of hashing list
                        setHash(victimHash, found);
                    } else {
                        //the victim is in the next, reChain it
                        while (m != -1) {
                            int next_of_m = next(m);
                            if (next_of_m == lastIndex) {
                                setNext(m, found);
                                break;
                            }
                            m = next_of_m;
                        }
                    }
                    mapSize--;
                }
            }
        }
        return result;
    }

    /**
     * extract the set under the form of an array of int
     * @return
     */
    public int[] extract() {
        return keys.extract();
    }

    /**
     * @return the size of the set
     */
    public int size() {
        return mapSize;
    }
}
