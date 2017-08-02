package gat.bitset;

import gat.AdditionalTypesPlugin;
import gat.MockStorage;
import greycat.Graph;
import greycat.GraphBuilder;
import greycat.Node;
import greycat.scheduler.NoopScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CTBitsetTest {
    private Graph g;
    private Node n;

    @BeforeEach
    public void initGraph() {
        MockStorage storage = new MockStorage();
        g = GraphBuilder.newBuilder()
                .withPlugin(new AdditionalTypesPlugin())
                .withStorage(storage)
                .withScheduler(new NoopScheduler())
                .build();
        g.connect(null);
        n = g.newNode(0, 0);
    }

    @Test
    public void cardinality() {
        CTBitset bitset = (CTBitset) n.getOrCreateCustom("bitset", CTBitset.NAME);
        bitset.add(0);
        bitset.add(10);
        bitset.add(17);
        bitset.add(2);
        bitset.add(4);

        assertEquals(5,bitset.cardinality());

    }

    @Test
    public void reload() {
        CTBitset bitset = (CTBitset) n.getOrCreateCustom("bitset", CTBitset.NAME);
        bitset.add(0);
        bitset.add(10);
        bitset.add(17);
        bitset.add(2);
        bitset.add(4);

        bitset.save();
        CTBitset bitset2 = (CTBitset) n.getOrCreateCustom("bitset", CTBitset.NAME);

        assertArrayEquals(bitset.getBitset().toIntArray(),bitset2.getBitset().toIntArray());
        assertEquals(bitset.cardinality(),bitset2.cardinality());
    }

    @Test
    public void next() {
        CTBitset bitset = (CTBitset) n.getOrCreateCustom("bitset", CTBitset.NAME);
        bitset.add(0);
        bitset.add(10);
        bitset.add(17);
        bitset.add(2);
        bitset.add(4);
        assertEquals(0,bitset.nextSetBit(0));
        assertEquals(17,bitset.nextSetBit(11));
    }

    @AfterEach
    public void close() {
        n.free();
        g.disconnect(null);
    }
}