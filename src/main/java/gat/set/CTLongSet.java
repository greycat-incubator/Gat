package gat.set;

import greycat.Constants;
import greycat.Type;
import greycat.base.BaseCustomType;
import greycat.struct.EStruct;
import greycat.struct.EStructArray;
import greycat.struct.LongArray;
import greycat.utility.HashHelper;

import java.util.Arrays;

public class CTLongSet extends BaseCustomType {
    private final LongArray keys;
    private int mapSize = 0;
    private int capacity = 0;

    public static final String NAME = "LongSet";

    public static final String KEYS = "keys";
    protected static final int KEYS_H = HashHelper.hash(KEYS);


    private int[] nexts = null;
    private int[] hashs = null;

    public CTLongSet(EStructArray p_backend) {
        super(p_backend);
        EStruct root = p_backend.root();
        if (root == null) {
            root = p_backend.newEStruct();
            p_backend.setRoot(root);
        }
        keys = (LongArray) root.getOrCreateAt(KEYS_H, Type.LONG_ARRAY);
        for (int i = 0; i < keys.size(); i++) {
            put(keys.get(i));
        }

    }

    protected long key(int i) {
        return keys.get(i);
    }

    private void addKey(long newValue) {
        keys.addElement(newValue);
    }

    private void setKey(int i, long newValue) {
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

    public boolean put(long insertKey) {
        boolean result = false;

        if (keys == null) {
            reallocate(Constants.MAP_INITIAL_CAPACITY);
            addKey(insertKey);
            setHash((int) HashHelper.longHash(insertKey, capacity * 2), 0);
            setNext(0, -1);
            mapSize++;
        } else {
            int hashCapacity = capacity * 2;
            int insertKeyHash = (int) HashHelper.longHash(insertKey, hashCapacity);
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
                    insertKeyHash = (int) HashHelper.longHash(insertKey, hashCapacity);
                    currentHash = hash(insertKeyHash);
                }
                addKey(insertKey);
                setHash((int) HashHelper.longHash(insertKey, capacity * 2), lastIndex);
                setNext(lastIndex, currentHash);
                mapSize++;
            }

        }
        return result;
    }

    public boolean contains(long requestKey) {
        boolean result = false;
        if (keys != null) {
            final int hashIndex = (int) HashHelper.longHash(requestKey, capacity * 2);
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

    public int index(long requestKey) {
        int result = -1;
        if (keys != null) {
            final int hashIndex = (int) HashHelper.longHash(requestKey, capacity * 2);
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

    public boolean remove(long requestKey) {
        boolean result = false;
        if (keys != null && mapSize != 0) {
            int hashCapacity = capacity * 2;
            int hashIndex = (int) HashHelper.longHash(requestKey, hashCapacity);
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
                int toRemoveHash = (int) HashHelper.longHash(requestKey, hashCapacity);
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
                    final long lastKey = key(lastIndex);
                    setKey(found, lastKey);
                    setNext(found, next(lastIndex));
                    int victimHash = (int) HashHelper.longHash(lastKey, hashCapacity);
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

    public long[] extract() {
        return keys.extract();
    }

    public int size() {
        return mapSize;
    }
}
