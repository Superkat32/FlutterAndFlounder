package net.superkat.flutterandflounder.flounderfest;

import com.google.common.collect.Maps;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import net.superkat.flutterandflounder.flounderfest.api.FlounderFestServerWorld;
import net.superkat.flutterandflounder.rendering.FlutterAndFlounderRendering;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public class FlounderFestManager extends PersistentState {
    private final Map<Integer, FlounderFest> flounderFests = Maps.newHashMap();
    private final ServerWorld world;
    private int nextAvailableId;

    public static PersistentState.Type<FlounderFestManager> getPersistentStateType(ServerWorld world) {
        return new PersistentState.Type<>(() -> new FlounderFestManager(world), nbt -> fromNbt(world, nbt), DataFixTypes.SAVED_DATA_RAIDS);
    }

    public FlounderFestManager(ServerWorld world) {
        this.world = world;
        nextAvailableId = 1;
    }

    public void createFlounderFest(FlounderFest flounderFest, ServerPlayerEntity player) {
        System.out.println("Creating new FlounderFest...");
        flounderFests.put(flounderFest.getId(), flounderFest);
    }

    public void tick() {

        Iterator<FlounderFest> allFlounderFests = this.flounderFests.values().iterator();

        while(allFlounderFests.hasNext()) {
            FlounderFest flounderFest = allFlounderFests.next();
            if(flounderFest.hasStopped()) {
                allFlounderFests.remove();
            } else {
                flounderFest.tick();
            }
        }
    }

    public void updateEnemyCount(FlounderFest flounderFest, boolean didYouDie) {
        flounderFest.updateEnemyCount(didYouDie);
    }

    public FlounderFest getOrCreateFlounderFest(ServerWorld world, BlockPos pos, int quota, int enemiesToBeSpawned) {
        FlounderFestServerWorld flounderWorld = (FlounderFestServerWorld) world;
        FlounderFest flounderFest = getFlounderFestAt(pos);
        return flounderFest != null ? flounderFest : createNewFlounderFest(world, pos, quota, enemiesToBeSpawned);
    }

    public FlounderFest createNewFlounderFest(ServerWorld world, BlockPos startingPos, int quota, int enemiesToBeSpawned) {
        return new FlounderFest(nextId(), world, startingPos, quota, enemiesToBeSpawned);
    }

    @Nullable
    public FlounderFest getFlounderFestAt(BlockPos pos) {
        return getFlounderFestAt(pos, 9216);
    }

    @Nullable
    public FlounderFest getFlounderFestAt(BlockPos pos, int searchDistance) {
        FlounderFest flounderFest = null;
        double distance = (double) searchDistance;

        for(FlounderFest searchedFlounderFest : flounderFests.values()) {
            double squaredDistance = searchedFlounderFest.getStartingPos().getSquaredDistance(pos);
            if(!searchedFlounderFest.hasStopped() && squaredDistance < distance) {
                flounderFest = searchedFlounderFest;
                distance = squaredDistance;
            }
        }
        return flounderFest;
    }

    private int nextId() {
        return ++nextAvailableId;
    }

    public static FlounderFestManager fromNbt(ServerWorld world, NbtCompound nbt) {
        FlounderFestManager flounderFestManager = new FlounderFestManager(world);

        return flounderFestManager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return null;
    }

    public static Vec3d getFlounderFestSkyColor(Vec3d initialColor) {
        double multiplier = FlutterAndFlounderRendering.skyChangeMultiplier;
        return initialColor.add(1 * multiplier, 0.2 * multiplier, 0.05 * multiplier);
    }
}
