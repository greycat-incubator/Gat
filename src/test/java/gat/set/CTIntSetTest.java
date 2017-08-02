package gat.set;

import gat.AdditionalTypesPlugin;
import gat.MockStorage;
import greycat.Callback;
import greycat.Graph;
import greycat.GraphBuilder;
import greycat.Node;
import greycat.scheduler.NoopScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CTIntSetTest {
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
    public void addEmpty() {
        CTIntSet intSet = (CTIntSet) n.getOrCreateCustom("longs", CTIntSet.NAME);
        intSet.put(0);
        assertTrue(intSet.contains(0));
        assertEquals(1, intSet.size());
    }

    @Test
    public void addSame() {
        CTIntSet intSet = (CTIntSet) n.getOrCreateCustom("longs", CTIntSet.NAME);
        intSet.put(0);
        intSet.put(0);
        assertEquals(1, intSet.size());
    }

    @Test
    public void reload() {
        CTIntSet intSet = (CTIntSet) n.getOrCreateCustom("longs", CTIntSet.NAME);
        intSet.put(0);
        intSet.put(1);
        intSet.put(3);
        intSet.put(4);
        intSet.put(0);

        CTIntSet intSet2 = (CTIntSet) n.getOrCreateCustom("longs", CTIntSet.NAME);
        assertEquals(intSet.size(), intSet2.size());
    }

    @Test
    public void indexOf() {
        CTIntSet intSet = (CTIntSet) n.getOrCreateCustom("longs", CTIntSet.NAME);
        intSet.put(0);
        intSet.put(1);
        intSet.put(5);
        intSet.put(4);

        assertEquals(0, intSet.index(0));
        assertEquals(2, intSet.index(5));
    }


    @AfterEach
    public void close() {
        n.free();
        g.disconnect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

            }
        });
    }
}