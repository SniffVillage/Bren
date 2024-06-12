package nl.sniffiandros.bren.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class MuzzleSmokeParticle extends AscendingParticle {

    protected MuzzleSmokeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.1F, 0.1F, 0.1F, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 1.0F, 2, 0.0F, true);
        this.setColor(1.0F,1.0F,1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        float f = (float) this.age/(this.getMaxAge()*2);
        this.setColor(1.0F - f, 1.0F - f, 1.0F - f);
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    public int getBrightness(float tint) {
        return 15728880;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new MuzzleSmokeParticle(clientWorld, d, e, f, g, h, i, 3.0F, this.spriteProvider);
        }
    }
}
