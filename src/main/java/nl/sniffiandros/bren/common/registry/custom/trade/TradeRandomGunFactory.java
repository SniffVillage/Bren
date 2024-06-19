package nl.sniffiandros.bren.common.registry.custom.trade;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import nl.sniffiandros.bren.common.registry.ItemReg;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TradeRandomGunFactory implements TradeOffers.Factory {

    private static final Set<Item> GUNS = ImmutableList.of(
            ItemReg.MACHINE_GUN,
            ItemReg.AUTO_GUN,
            ItemReg.RIFLE,
            ItemReg.SHOTGUN,
            ItemReg.REVOLVER
    ).stream().collect(ImmutableSet.toImmutableSet());

    private final int price;
    private final int exp;

    public TradeRandomGunFactory(int price, int exp) {
        this.price = price;
        this.exp = exp;
    }

    @Nullable
    @Override
    public TradeOffer create(Entity entity, Random random) {

        Item item = (Item) GUNS.toArray()[random.nextBetween(0, GUNS.size() - 1)];

        int i = 5 + random.nextInt(15);
        ItemStack itemStack = EnchantmentHelper.enchant(random, new ItemStack(item), i, false);
        int j = Math.min(this.price + i, 64);
        ItemStack itemStack2 = new ItemStack(Items.EMERALD, j);



        return new TradeOffer(itemStack2, itemStack,1,this.exp,.23f);
    }
}
