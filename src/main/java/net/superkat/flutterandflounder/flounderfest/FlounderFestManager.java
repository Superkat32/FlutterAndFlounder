package net.superkat.flutterandflounder.flounderfest;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

    //NOTE:
    //While this class does extend the PersistentState, it does NOT have any ability to save its data upon closing Minecraft.
    //I tried to get things figured out, but decided my time would be better spent elsewhere.
    //If, by some random chance, you are a random person looking to PR to this, here's a free idea lol
    //If you do end up doing that, make sure to remove the
    //ServerPlayConnectionEvents.DISCONNECT registry in FlutterAndFlounderMain
    public static PersistentState.Type<FlounderFestManager> getPersistentStateType(ServerWorld world) {
        return new PersistentState.Type<>(() -> new FlounderFestManager(world), nbt -> fromNbt(world, nbt), DataFixTypes.SAVED_DATA_RAIDS);
    }

    public FlounderFestManager(ServerWorld world) {
        this.world = world;
        nextAvailableId = 0;
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

    public void updateQuota(FlounderFest flounderFest) {
        updateQuota(flounderFest, 1);
    }

    public void updateQuota(FlounderFest flounderFest, int amount) {
        flounderFest.updateQuota(amount);
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

    public void removeAllFlounderFests() {
        for (FlounderFest flounderFest : flounderFests.values()) {
            flounderFest.invalidate();
        }
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
        long time = MinecraftClient.getInstance().world.getTimeOfDay() % 24000;
        if(time >= 12000) {
            //reduces the effect a lot during nighttime to not hurt people's eyes
            multiplier = MathHelper.clamp(multiplier - (time * 0.00002) * 2, 0, 1);
        }
        return initialColor.add(1 * multiplier, 0.2 * multiplier, 0.05 * multiplier);
    }
}
