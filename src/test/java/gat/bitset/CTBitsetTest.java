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
    private Graph graph;
    private Node n;

    @BeforeEach
    public void initGraph(){
        MockStorage storage = new MockStorage();
        Graph g = GraphBuilder.newBuilder()
                .withPlugin(new AdditionalTypesPlugin())
                .withStorage(storage)
                .withScheduler(new NoopScheduler())
                .build();
        g.connect(null);
        n = g.newNode(0, 0);
    }

    @Test
    public void addEmpty(){

    }

    @AfterEach
    public void close(){
        n.free();
        graph.disconnect(null);
    }
}