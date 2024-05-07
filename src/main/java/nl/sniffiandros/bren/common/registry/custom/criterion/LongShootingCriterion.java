package nl.sniffiandros.bren.common.registry.custom.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.IGunUser;

public class LongShootingCriterion extends AbstractCriterion<LongShootingCriterion.Conditions> {
    static final Identifier ID = new Identifier(Bren.MODID, "shooting");


    public Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("firearm"));
        return new Conditions(lootContextPredicate, itemPredicate);
    }


    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate firearm;

        public Conditions(LootContextPredicate entity, ItemPredicate itemPredicate) {
            super(ID, entity);
            this.firearm = itemPredicate;
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("firearm", this.firearm.toJson());
            return jsonObject;
        }


        public boolean test(PlayerEntity player, ItemStack stack) {
            return ((IGunUser)player).shootingDuration() > 80 && player.getActiveItem() == stack && this.firearm.test(stack);
        }
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        trigger(player, conditions -> conditions.test(player, stack));
    }
}
