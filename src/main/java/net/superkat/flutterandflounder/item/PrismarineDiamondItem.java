package net.superkat.flutterandflounder.item;

import net.minecraft.block.Blocks;
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
                double d = world.getRandom().nextGaussian() * 0.02;
                double e = world.getRandom().nextGaussian() * 0.02;
                double f = world.getRandom().nextGaussian() * 0.02;
                world.addParticle(ParticleTypes.SPLASH, entity.getParticleX(1.0), entity.getRandomBodyY() + 1.0, entity.getParticleZ(1.0), d, e, f);
            }
        } else if (status == ActivationStatus.BEGIN) {
            ticksSinceBegin++;
            if(ticksSinceBegin % 5 == 0) {
                entity.getWorld().addBlockBreakParticles(entity.getBlockPos(), Blocks.PRISMARINE.getDefaultState());
            }
            if(ticksSinceBegin >= 40) {
                stack.decrement(1);
                status = ActivationStatus.NONE;
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        if(FlounderFestApi.getFlounderFestManager((ServerWorld) world).getFlounderFestAt(user.getBlockPos(), 100) != null) {
            user.sendMessage(Text.translatable("item.flutterandflounder.prismarine_diamond.fail").formatted(Formatting.RED));
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        if(status == ActivationStatus.NONE) {
            status = ActivationStatus.WARNING;
            ticksSinceWarning = 0;
            user.playSound(SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.PLAYERS, 1, 1);
        } else if (status == ActivationStatus.WARNING) {
            status = ActivationStatus.BEGIN;
            ticksSinceBegin = 0;
            user.playSound(SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1, 1);
            startFlounderFest((ServerPlayerEntity) user);
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
