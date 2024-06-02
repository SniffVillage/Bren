package nl.sniffiandros.bren.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundCategory;
import nl.sniffiandros.bren.common.registry.SoundReg;

public class CasingParticle extends AscendingParticle {

    protected float bounce = 0.9f;
    protected boolean made_sound = false;

    protected CasingParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.1F, 0.1F, 0.1F, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 1.0F, 32, 2.0F, true);
        this.angle = (float) (Math.PI * world.getRandom().nextFloat());
        this.setColor(2.0F,2.0F,2.0F);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevAngle = this.angle;
        if (!this.onGround) {
            this.angle += 0.3f;
        } else {
            this.velocityY += this.bounce;
            this.bounce = 0;
            if (!this.made_sound) {
                this.made_sound = true;

                this.world.playSound(this.x, this.y, this.z, SoundReg.PARTICLE_CASING_BOUNCE, SoundCategory.BLOCKS, 1.0F,1.0F - (this.world.getRandom().nextFloat() - 0.5F)/8, false);
            }
        }
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new CasingParticle(clientWorld, d, e, f, g, h, i, .75F, this.spriteProvider);
        }
    }
}
