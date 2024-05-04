package nl.sniffiandros.bren.common.registry;

import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.mixin.StructurePoolAccessor;

import java.util.ArrayList;
import java.util.List;

public class StructureRegistry {
    private static final RegistryKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier("minecraft", "empty"));
    private static final Identifier plainsPoolLocation = new Identifier("minecraft:village/plains/houses");
    private static final Identifier desertPoolLocation = new Identifier("minecraft:village/desert/houses");
    private static final Identifier savannaPoolLocation = new Identifier("minecraft:village/savanna/houses");
    private static final Identifier snowyPoolLocation = new Identifier("minecraft:village/snowy/houses");
    private static final Identifier taigaPoolLocation = new Identifier("minecraft:village/taiga/houses");

    public static void registerJigsaws(MinecraftServer server) {
        Registry<StructurePool> templatePoolRegistry = server.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorListRegistry = server.getRegistryManager().get(RegistryKeys.PROCESSOR_LIST);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, Bren.MODID + ":village/plains/houses/plains_gun_store_0", 5);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertPoolLocation, Bren.MODID + ":village/desert/houses/desert_gun_store_0", 5);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, Bren.MODID + ":village/savanna/houses/savanna_gun_store_0", 5);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyPoolLocation, Bren.MODID + ":village/snowy/houses/snowy_gun_store_0", 5);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, taigaPoolLocation, Bren.MODID + ":village/taiga/houses/taiga_gun_store_0", 5);

    }

    public static void addBuildingToPool(Registry<StructurePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, Identifier poolRL, String nbtPieceRL, int weight) {
        RegistryEntry<StructureProcessorList> processorList = processorListRegistry.entryOf(EMPTY_PROCESSOR_LIST_KEY);

        StructurePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) return;

        SinglePoolElement piece = SinglePoolElement.ofProcessedSingle(nbtPieceRL, processorList).apply(StructurePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            ((StructurePoolAccessor) pool).getTemplates().add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(((StructurePoolAccessor) pool).getRawTemplates());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructurePoolAccessor) pool).setRawTemplates(listOfPieceEntries);
    }
}
