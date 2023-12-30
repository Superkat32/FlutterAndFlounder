package net.superkat.flutterandflounder.item;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.flounderfest.api.FlounderFestApi;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrismarineDiamondItem extends Item {
    ActivationStatus status = ActivationStatus.NONE;
    private int ticksSinceWarning = 0;
    private int ticksSinceBegin = 0;
    public PrismarineDiamondItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.flutterandflounder.prismarine_diamond.tooltip").formatted(Formatting.AQUA));
        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.flutterandflounder.prismarine_diamond.shiftedtooltip").formatted(Formatting.YELLOW));
        } else {
            tooltip.add(Text.translatable("item.flutterandflounder.prismarine_diamond.expand").formatted(Formatting.DARK_AQUA, Formatting.ITALIC));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(status == ActivationStatus.WARNING) {
            ticksSinceWarning++;
            if(ticksSinceWarning >= 240) {
                status = ActivationStatus.NONE;
            }
            if(ticksSinceWarning % 2 == 0) { //every 2 ticks
                if(world.isClient) {
                    double d = world.getRandom().nextGaussian() * 0.02;
                    double e = world.getRandom().nextGaussian() * 0.02;
                    double f = world.getRandom().nextGaussian() * 0.02;
                    world.addParticle(ParticleTypes.SPLASH, entity.getParticleX(1.0), entity.getRandomBodyY() + 1.0, entity.getParticleZ(1.0), d, e, f);
                }
            }
        } else if (status == ActivationStatus.BEGIN) {
            if(ticksSinceBegin == 0) {
                stack.decrement(1);
            }
            ticksSinceBegin++;
            if(ticksSinceBegin % 5 == 0) {
                if (world.isClient) {
                    entity.getWorld().addBlockBreakParticles(entity.getBlockPos(), Blocks.PRISMARINE.getDefaultState());
                    if(!MinecraftClient.getInstance().isInSingleplayer()) { //could use packets but I'm lazy
                        status = ActivationStatus.NONE;
                    }
                }
            }
            if(ticksSinceBegin >= 40) {
                status = ActivationStatus.NONE;
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient) {
            if(FlounderFestApi.getFlounderFestManager((ServerWorld) world).getFlounderFestAt(user.getBlockPos()) != null) {
                user.sendMessage(Text.translatable("item.flutterandflounder.prismarine_diamond.fail").formatted(Formatting.RED));
                return TypedActionResult.pass(user.getStackInHand(hand));
            }
        }

        if(status == ActivationStatus.NONE) {
            ticksSinceWarning = 0;
            if(world.isClient) {
                user.playSound(SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.PLAYERS, 1, 1);
                if(!MinecraftClient.getInstance().isInSingleplayer()) { //could use packets but I'm lazy
                    status = ActivationStatus.WARNING;
                }
            } else {
                status = ActivationStatus.WARNING;
            }
        } else if (status == ActivationStatus.WARNING) {
            ticksSinceBegin = 0;
            if (world.isClient) {
                if(!MinecraftClient.getInstance().isInSingleplayer()) { //could use packets but I'm still lazy
                    status = ActivationStatus.BEGIN;
                }
                user.playSound(SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1, 1);
            } else {
                status = ActivationStatus.BEGIN;
                startFlounderFest((ServerPlayerEntity) user);
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return status == ActivationStatus.WARNING || status == ActivationStatus.BEGIN;
    }

    public void startFlounderFest(ServerPlayerEntity player) {
        FlounderFestApi.startFlounderFest(player);
    }


    static enum ActivationStatus {
        NONE,
        WARNING,
        BEGIN;
    }
}
