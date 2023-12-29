package net.superkat.flutterandflounder.flounderfest;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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
        return new PersistentState.Type<>(() -> new FlounderFestManager(world), nbt -> fromNbt(world, nbt), null);
    }

    public FlounderFestManager(ServerWorld world) {
        this.world = world;
        nextAvailableId = 0;
        this.markDirty();
    }

    /**
     * Finalize a FlounderFest.
     *
     * @param flounderFest The FlounderFest to be created.
     * @param player The player responsible for creating the FlounderFest
     */
    public void createFlounderFest(FlounderFest flounderFest, ServerPlayerEntity player) {
        System.out.println("Creating new FlounderFest...");
        flounderFests.put(flounderFest.getId(), flounderFest);
        this.markDirty();
    }

    /**
     * Called every tick by the ServerWorldMixin.
     */
    public void tick() {

        Iterator<FlounderFest> allFlounderFests = this.flounderFests.values().iterator();

        while(allFlounderFests.hasNext()) {
            FlounderFest flounderFest = allFlounderFests.next();
            if(flounderFest.hasStopped()) {
                allFlounderFests.remove();
                this.markDirty();
            } else {
                flounderFest.tick();
            }
        }
    }

    public void updateQuota(FlounderFest flounderFest) {
        updateQuota(flounderFest, 1);
    }

    /**
     * Update a FlounderFest's quota by a specific amount.
     *
     * @param flounderFest The FlounderFest the quota should be updated in
     * @param amount The amount of the quota update
     */
    public void updateQuota(FlounderFest flounderFest, int amount) {
        flounderFest.updateQuota(amount);
    }

    public FlounderFest getOrCreateFlounderFest(ServerWorld world, BlockPos pos, int quota, int enemiesToBeSpawned) {
        FlounderFestServerWorld flounderWorld = (FlounderFestServerWorld) world;
        FlounderFest flounderFest = getFlounderFestAt(pos);
        return flounderFest != null ? flounderFest : createNewFlounderFest(world, pos, quota, enemiesToBeSpawned);
    }

    /**
     * Create a new FlounderFest with its unique ID and specific parameters.
     *
     * @param world The world the FlounderFest should be in.
     * @param startingPos The starting blockpos of the FlounderFest.
     * @param quota The quota amount the FlounderFest should have.
     * @param enemiesToBeSpawned The enemies to be spawned in the FlounderFest. Use "-1" for infinite enemies.
     * @return Returns a new FlounderFest using the parameters.
     */
    public FlounderFest createNewFlounderFest(ServerWorld world, BlockPos startingPos, int quota, int enemiesToBeSpawned) {
        return new FlounderFest(nextId(), world, startingPos, quota, enemiesToBeSpawned);
    }

    /**
     * Find the closest FlounderFest in a 96 block range.
     *
     * @param pos The center BlockPos of the searching.
     * @return Returns the closest FlounderFest in a 96 block range, or null if none were found.
     */
    @Nullable
    public FlounderFest getFlounderFestAt(BlockPos pos) {
        return getFlounderFestAt(pos, 9216);
    }

    /**
     * @param pos The center BlockPos of the searching.
     * @param searchDistance The distance of the searching. 9216 = 96 blocks.
     * @return Returns the closest FlounderFest within the search distance, or null if none were found.
     */
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

    /**
     * Removes ALL FlounderFests in the server world.
     */
    public void removeAllFlounderFests() {
        for (FlounderFest flounderFest : flounderFests.values()) {
            flounderFest.invalidate();
        }
    }

    public static FlounderFestManager fromNbt(ServerWorld world, NbtCompound nbt) {
        FlounderFestManager flounderFestManager = new FlounderFestManager(world);
        flounderFestManager.nextAvailableId = nbt.getInt("NextAvailableID");

        NbtList nbtList = nbt.getList("FlounderFests", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            FlounderFest flounderFest = new FlounderFest(world, nbtCompound);
            flounderFestManager.flounderFests.put(flounderFest.getId(), flounderFest);
        }

        return flounderFestManager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("NextAvailableID", this.nextAvailableId);

        NbtList nbtList = new NbtList();

        for (FlounderFest flounderFest : this.flounderFests.values()) {
            NbtCompound nbtCompound = new NbtCompound();
            flounderFest.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }

        nbt.put("FlounderFests", nbtList);
        return nbt;
    }

    /**
     * Updates the sky color to have a hint of red. Only called when a player is in a FlounderFest.
     *
     * @param initialColor The initial sky color.
     * @return Returns the updated sky color from the FlounderFest(hint of red). Changes based on time of day.
     */
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
