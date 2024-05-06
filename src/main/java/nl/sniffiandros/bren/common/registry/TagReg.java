package nl.sniffiandros.bren.common.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;

public class TagReg {
    public static final TagKey<Item> MEDIUM_MAGAZINES = itemTag("magazines/medium_magazines");
    public static final TagKey<Item> SHORT_MAGAZINES = itemTag("magazines/short_magazines");
    public static final TagKey<DamageType> IS_BULLET = damageTypeTag("is_bullet");

    private static TagKey<Item> itemTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(Bren.MODID, name));
    }
    private static TagKey<DamageType> damageTypeTag(String name) {
        return TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Bren.MODID, name));
    }
}
