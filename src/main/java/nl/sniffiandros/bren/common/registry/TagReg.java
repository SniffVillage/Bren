package nl.sniffiandros.bren.common.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;

public class TagReg {
    public static final TagKey<Item> MEDIUM_MAGAZINES = itemTag("magazines/medium_magazines");
    public static final TagKey<Item> SHORT_MAGAZINES = itemTag("magazines/short_magazines");

    private static TagKey<Item> itemTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(Bren.MODID, name));
    }
}
