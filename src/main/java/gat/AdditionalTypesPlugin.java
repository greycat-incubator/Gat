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
package gat;

import gat.bitmap.CTBitmap;
import gat.bitset.CTBitset;
import gat.set.CTIntSet;
import gat.set.CTLongSet;
import greycat.Graph;
import greycat.plugin.Plugin;
import greycat.plugin.TypeFactory;
import greycat.struct.EStructArray;

public class AdditionalTypesPlugin implements Plugin {

    @Override
    public void start(Graph graph) {
        graph.typeRegistry()
                .getOrCreateDeclaration(CTBitset.NAME)
                .setFactory(new TypeFactory() {
                    @Override
                    public Object wrap(final EStructArray backend) {
                        return new CTBitset(backend);
                    }
                });
        graph.typeRegistry()
                .getOrCreateDeclaration(CTBitmap.NAME)
                .setFactory(new TypeFactory() {
                    @Override
                    public Object wrap(final EStructArray backend) {
                        return new CTBitmap(backend);
                    }
                });
        graph.typeRegistry()
                .getOrCreateDeclaration(CTIntSet.NAME)
                .setFactory(new TypeFactory() {
                    @Override
                    public Object wrap(final EStructArray backend) {
                        return new CTIntSet(backend);
                    }
                });
        graph.typeRegistry()
                .getOrCreateDeclaration(CTLongSet.NAME)
                .setFactory(new TypeFactory() {
                    @Override
                    public Object wrap(final EStructArray backend) {
                        return new CTLongSet(backend);
                    }
                });
    }

    @Override
    public void stop() {

    }
}
