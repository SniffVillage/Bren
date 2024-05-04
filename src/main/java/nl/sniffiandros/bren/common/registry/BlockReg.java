package nl.sniffiandros.bren.common.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.custom.block.WorkbenchBlock;

public class BlockReg {
    public static final Block WORKBENCH = registerBlock("workbench", new WorkbenchBlock(FabricBlockSettings.create().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.LANTERN).instrument(Instrument.BASEDRUM).strength(3.5F)));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Bren.MODID, name), block);
    }

    public static void reg() {
    }
}
