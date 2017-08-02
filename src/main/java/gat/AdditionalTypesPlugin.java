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
