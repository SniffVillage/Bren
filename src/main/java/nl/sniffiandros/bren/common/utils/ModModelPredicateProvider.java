package nl.sniffiandros.bren.common.utils;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.registry.ItemReg;
import nl.sniffiandros.bren.common.registry.custom.types.GunWithMagItem;
import nl.sniffiandros.bren.common.registry.custom.MagazineItem;

public class ModModelPredicateProvider {
    public static void regModels() {
        regGun(ItemReg.MACHINE_GUN, true);
        regGun(ItemReg.AUTO_GUN, true);
        regGun(ItemReg.RIFLE, false);
        regGun(ItemReg.NETHERITE_MACHINE_GUN, true);
        regGun(ItemReg.NETHERITE_AUTO_GUN, true);
        regGun(ItemReg.NETHERITE_RIFLE, false);
        regMag(ItemReg.MAGAZINE);
        regMag(ItemReg.CLOTHED_MAGAZINE);
        regMag(ItemReg.SHORT_MAGAZINE);
    }


    private static void regGun(Item machineGun, boolean colorable) {
        ModelPredicateProviderRegistry.register(machineGun, new Identifier("has_magazine"),
                (stack, world, entity, seed) -> GunWithMagItem.hasMagazine(stack) ? 1.0f : 0.0f);
        if (colorable) {
            ModelPredicateProviderRegistry.register(machineGun, new Identifier("has_colorable_magazine"),
                    (stack, world, entity, seed) -> GunWithMagItem.hasColorableMagazine(stack) ? 1.0f : 0.0f);
        }

    }
    private static void regMag(Item magazine) {
        ModelPredicateProviderRegistry.register(magazine, new Identifier("is_full"),
                (stack, world, entity, seed) -> !MagazineItem.isEmpty(stack) ? 1.0f : 0.0f);
    }
}
