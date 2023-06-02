package yezhiyi9670.lightspeedboat.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import yezhiyi9670.lightspeedboat.config.ConfigData;
import yezhiyi9670.lightspeedboat.config.ConfigManager;

@Mixin(BoatEntity.class)
public abstract class MixinBoatEntity extends Entity {
    @Shadow
    private BoatEntity.Location location;
    @Shadow
    private boolean onBubbleColumnSurface;
    @Shadow
    private boolean bubbleColumnIsDrag;

    public MixinBoatEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    private boolean isLightspeed() {
        double y = this.getY();
        double yRel = y - Math.floor(y);
        ConfigData config = ConfigManager.get();
        double yMax = config.slabHeightMax;
        double yMin = config.slabHeightMin;
        BlockState blockUnder = this.getWorld().getBlockState(this.getBlockPos());

        // Speed min
        if(this.getVelocity().length() < config.lightspeedThershold / 20) {
            return false;
        }

        if (blockUnder.getBlock() != Blocks.WATER && blockUnder.getBlock() != Blocks.BUBBLE_COLUMN) { // Do not detect regular water boat
            // Slab
            if(!config.requireLevitating) {
                if ((this.location == BoatEntity.Location.ON_LAND || this.location == BoatEntity.Location.IN_WATER) && yMin <= yRel && yRel <= yMax) {
                    return true;
                }
            }
            // Bubble
            double maxY = this.getBoundingBox().maxY;
            if (this.location == BoatEntity.Location.IN_WATER && Math.abs(getVelocity().y) < 1e-6 && Math.abs(maxY - Math.round(maxY)) < 1e-6) {
                return true;
            }
        }

        return false;
    }

    @Redirect(
            method = "updateVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/BoatEntity;setVelocity(DDD)V",
                    ordinal = 0
            )
    )
    private void lightspeedboat$updateVelocity$setVelocity(BoatEntity instance, double x, double y, double z) {
        ConfigData config = ConfigManager.get();
        boolean lightspeed = this.isLightspeed();
        Vec3d vel = getVelocity();
        if(lightspeed && vel.length() < config.maxLightspeed / 20) {
            setVelocity(vel.x * (1 - config.lightspeedDrag), y, vel.z * (1 - config.lightspeedDrag));
        } else {
            setVelocity(x, y, z);
        }
    }

    @Redirect(
            method = "updatePaddles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/BoatEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
            )
    )
    private void lightspeedboat$updatePaddles$setVelocity(BoatEntity instance, Vec3d vec3d) {
        boolean lightspeed = this.isLightspeed();
        ConfigData config = ConfigManager.get();
        if(!lightspeed || config.allowPlayerDrive) {
            setVelocity(vec3d);
        }
    }

    @Redirect(
            method = "updatePaddles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/BoatEntity;setYaw(F)V"
            )
    )
    private void lightspeedboat$updatePaddles$setYaw(BoatEntity instance, float v) {
        boolean lightspeed = this.isLightspeed();
        ConfigData config = ConfigManager.get();
        if(!lightspeed || config.allowPlayerTurn) {
            setYaw(v);
        }
    }
}
