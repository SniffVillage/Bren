package nl.sniffiandros.bren.common.utils;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public class GunHelper {
    private static final TrackedData<Integer> STATE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static void dataTracker(DataTracker dataTracker) {
        dataTracker.startTracking(STATE, 0);
    }

    public static GunHelper.GunStates getGunState(DataTracker dataTracker) {
        return GunHelper.GunStates.byIndex(dataTracker.get(STATE));
    }

    public static void setGunState(GunHelper.GunStates state, DataTracker dataTracker) {
        dataTracker.set(STATE, state.getId());
    }

    public static void readCustomDataFromNbt(NbtCompound nbt, DataTracker dataTracker) {
        setGunState(GunHelper.GunStates.byIndex(nbt.getInt("MachineGunState")), dataTracker);
    }

    public static void writeCustomDataToNbt(NbtCompound nbt, DataTracker dataTracker) {
        nbt.putInt("MachineGunState", getGunState(dataTracker).getId());
    }

    public enum GunStates {
        NORMAL(0),
        RELOADING(1);

        final int id;

        private static final IntFunction<GunStates> BY_ID = ValueLists.createIdToValueFunction(GunStates::getId, values(), ValueLists.OutOfBoundsHandling.CLAMP);

        GunStates(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static GunStates byIndex(int index) {
            return BY_ID.apply(index);
        }
    }
}
