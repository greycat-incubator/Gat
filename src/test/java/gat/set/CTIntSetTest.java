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