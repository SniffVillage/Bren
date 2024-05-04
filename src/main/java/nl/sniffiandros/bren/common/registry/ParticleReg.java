package nl.sniffiandros.bren.common.registry;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;

public class ParticleReg {
    public static final DefaultParticleType MUZZLE_SMOKE_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType AIR_RING_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType CASING_PARTICLE = FabricParticleTypes.simple();

    public static void reg() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(Bren.MODID, "muzzle_smoke"),
                MUZZLE_SMOKE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(Bren.MODID, "air_ring"),
                AIR_RING_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(Bren.MODID, "casing"),
                CASING_PARTICLE);
    }
}
