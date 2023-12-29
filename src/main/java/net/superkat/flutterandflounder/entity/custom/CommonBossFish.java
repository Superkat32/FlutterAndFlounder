package net.superkat.flutterandflounder.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.flounderfest.FlounderFest;
import net.superkat.flutterandflounder.flounderfest.api.FlounderFestApi;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class CommonBossFish extends HostileEntity implements GeoEntity {
    protected CommonBossFish(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void updateFlounderFestQuota(ServerWorld world, BlockPos pos) {
        FlounderFest flounderFest = FlounderFestApi.getFlounderFestAt(world, pos);
        if(flounderFest != null) {
            flounderFest.updateQuota(1);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if(!this.getWorld().isClient && damageSource.getAttacker() instanceof PlayerEntity) {
            updateFlounderFestQuota((ServerWorld) this.getWorld(), this.getBlockPos());
        }
        super.onDeath(damageSource);
    }
}
