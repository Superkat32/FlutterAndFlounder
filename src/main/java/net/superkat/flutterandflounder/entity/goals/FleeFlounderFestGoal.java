package net.superkat.flutterandflounder.entity.goals;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.superkat.flutterandflounder.flounderfest.FlounderFest;
import net.superkat.flutterandflounder.flounderfest.api.FlounderFestApi;
import org.jetbrains.annotations.Nullable;

public class FleeFlounderFestGoal extends Goal {
    public MobEntity flounderFestEntity = null;
    @Nullable
    FlounderFest flounderFest = null;

    BlockPos fleePos = null;

    public FleeFlounderFestGoal(MobEntity flounderFestEntity) {
        this.flounderFestEntity = flounderFestEntity;
    }

    @Override
    public boolean canStart() {
        boolean shouldFlee = false;
        FlounderFest flounderFest = FlounderFestApi.getFlounderFestAt((ServerWorld) flounderFestEntity.getWorld(), flounderFestEntity.getBlockPos(), 2000);
        this.flounderFest = flounderFest;
        if(flounderFest != null) {
            if(flounderFest.shouldMobsFlee()) {
                shouldFlee = true;
            }
        } else {
//            flounderFestEntity.discard();
        }
        return shouldFlee;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if(flounderFest != null) {
            BlockPos flounderFestCenter = flounderFest.getStartingPos();
            if(fleePos == null) {
                fleePos = new BlockPos(
                        flounderFestCenter.getX() + flounderFestEntity.getRandom().nextInt() * (flounderFestEntity.getRandom().nextBoolean() ? 1 : -1),
                        flounderFestCenter.getY(),
                        flounderFestCenter.getZ() + flounderFestEntity.getRandom().nextInt() * (flounderFestEntity.getRandom().nextBoolean() ? 1 : -1)
                );
            }
            flounderFestEntity.getMoveControl().moveTo(fleePos.getX(), fleePos.getY(), fleePos.getZ(), 1);
            flounderFestEntity.setTarget(null);
        }
    }
}
