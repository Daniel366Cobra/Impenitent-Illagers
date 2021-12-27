package com.daniel366cobra.pitilesspillagers.item;

import com.daniel366cobra.pitilesspillagers.ModSounds;
import com.daniel366cobra.pitilesspillagers.PitilessPillagers;
import com.daniel366cobra.pitilesspillagers.entity.projectile.MusketProjectileEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MusketItem extends Item{
    private static final String LOADED_KEY = "Loaded";


    private boolean cocked = false;
    private boolean loaded = false;


    public MusketItem(Item.Settings settings) {
        super(settings);
    }

    //Sets weapon in use (firing/loading)
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack weaponItemStack = user.getStackInHand(hand);
        if (isLoaded(weaponItemStack)) {

            shoot(world, user, hand, weaponItemStack);
            setLoaded(weaponItemStack, false);
            return TypedActionResult.consume(weaponItemStack);
        }
        if (!findAmmo(user).isEmpty()) {
            if (!isLoaded(weaponItemStack)) {
                this.cocked = false;
                this.loaded = false;
                user.setCurrentHand(hand);
            }
            return TypedActionResult.consume(weaponItemStack);
        }
        return TypedActionResult.fail(weaponItemStack);
    }

    public static boolean hasAmmo(PlayerEntity user) {
        return findAmmo(user).isEmpty();
    }


    public static ItemStack findAmmo(PlayerEntity user) {
        boolean userCreative = user.isCreative();
        ItemStack ammoStack;
        if (isAmmo(user.getMainHandStack())) {
            ammoStack = user.getMainHandStack();
            return ammoStack;
        } else if (isAmmo(user.getOffHandStack())) {
            ammoStack = user.getOffHandStack();
            return ammoStack;
        } else {
            for (int iter = 0; iter < user.getInventory().size(); iter++) {
                if (isAmmo(user.getInventory().getStack(iter))) {
                    ammoStack = user.getInventory().getStack(iter);
                    return ammoStack;
                }
            }
            ammoStack = ItemStack.EMPTY;
        }
        if (ammoStack.isEmpty() && userCreative) {
            ammoStack = new ItemStack(PitilessPillagers.MUSKET_BALL);
        }
        return ammoStack;
    }

    public static boolean isAmmo(ItemStack stack) {
        return stack.getItem() instanceof MusketBallItem;
    }

    private static boolean checkAndConsumeAmmo(PlayerEntity player) {

        boolean creative = player.isCreative();

        ItemStack ammoStack = findAmmo(player);

        if (ammoStack.isEmpty() && creative) {
            ammoStack = new ItemStack(PitilessPillagers.MUSKET_BALL);
        }

        return attemptConsumeAmmo(player, ammoStack, creative);
    }

    private static boolean attemptConsumeAmmo(PlayerEntity player, ItemStack ammoStack, boolean creative)
    {
        if (creative) { return true; }

            if (!ammoStack.isEmpty()) //If found ammo, consume 1 unit
            {
                ammoStack.decrement(1);
                return true;
            }
            else
            {
                return false;
            }
        }


    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int timeLoading = this.getMaxUseTime(stack) - remainingUseTicks;
        float loadProgress = getLoadProgress(timeLoading, stack);

        if (loadProgress >= 1.0f && !isLoaded(stack) && checkAndConsumeAmmo((PlayerEntity) user)) {
            setLoaded(stack, true);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_COCK_FULL, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
        else
        {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_MISFIRE, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    public static boolean isLoaded(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean(LOADED_KEY);
    }

    public static void setLoaded(ItemStack stack, boolean charged) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putBoolean(LOADED_KEY, charged);
    }

    public static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack weaponStack) {
    boolean creative = ((PlayerEntity)shooter).isCreative();

        if (!shooter.isWet())
        {
            Vec3d shooterLookVec3d = Vec3d.fromPolar(shooter.getPitch(), shooter.getYaw());
            Vec3d firePosition = new Vec3d(shooter.getX(), shooter.getY() + shooter.getEyeHeight(shooter.getPose()), shooter.getZ());
            for (int i = 0; i < 30; i++)
            {
                float velMagnModifier = 1.0F - 0.25F + shooter.getRandom().nextFloat() * 0.5F;

                float particleVelX = (float)shooterLookVec3d.x * velMagnModifier + (-0.15F + shooter.getRandom().nextFloat() * 0.3F);
                float particleVelY = (float)shooterLookVec3d.y * velMagnModifier + (-0.15F + shooter.getRandom().nextFloat() * 0.3F);
                float particleVelZ = (float)shooterLookVec3d.z * velMagnModifier + (-0.15F + shooter.getRandom().nextFloat() * 0.3F);


                world.addParticle(ParticleTypes.CLOUD, firePosition.x, firePosition.y, firePosition.z, particleVelX, particleVelY, particleVelZ);
            }
            if (!world.isClient())
            {
                //Create and spawn the bullet
                MusketProjectileEntity bullet = new MusketProjectileEntity(world, shooter, 3.0D, false);
                bullet.setVelocity(shooter, shooter.getPitch(), shooter.getYaw(), 6F, shooter.isSneaking()? 0.5F : 3.0F);
                world.spawnEntity(bullet);

                //Shot sound
                world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.MUSKET_SHOT, SoundCategory.PLAYERS, 2.0F, 0.5F);


                //Degrade the durability
                if (!creative)
                {
                    weaponStack.damage(1, shooter, (entity) -> {
                        entity.sendToolBreakStatus(shooter.getActiveHand());
                    });

                    //additionally have a chance to explode if very low
                    DamageSource gunexplosiondamage;
                    int remainingLife = weaponStack.getMaxDamage() - weaponStack.getDamage();
                    if (remainingLife <= 10)
                    {
                        if (shooter.getRandom().nextFloat() < (1.0F / (remainingLife + 1)))
                        {
                            ((PlayerEntity) shooter).getInventory().removeOne(weaponStack);
                            gunexplosiondamage = new EntityDamageSource("gunexplosion", shooter);
                            world.createExplosion(shooter, shooter.getX(), shooter.getY() + shooter.getEyeHeight(shooter.getPose()) - 0.1D, shooter.getZ(), 1.5F, false, Explosion.DestructionType.NONE);
                            shooter.damage(gunexplosiondamage, 18.0F);

                        }
                    }
                }
            }

        }
        else
        {
            world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.MUSKET_MISFIRE, SoundCategory.PLAYERS, 2.0F, 1.0F);

        }
    }


    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) {
            float f = (float)(stack.getMaxUseTime() - remainingUseTicks) / (float) MusketItem.getLoadTime(stack);
            if (f < 0.2f) {
                this.cocked = false;
                this.loaded = false;
            }
            if (f >= 0.2f && !this.cocked) {
                this.cocked = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_COCK_START, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
            if (f >= 0.5f && !this.loaded) {
                this.loaded = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_COCK_HALF, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return MusketItem.getLoadTime(stack) + 3;
    }

    public static int getLoadTime(ItemStack stack) {
        return 25;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    private static float getLoadProgress(int useTicks, ItemStack stack) {
        float f = (float)useTicks / (float) MusketItem.getLoadTime(stack);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!MusketItem.isLoaded(stack)) {
            return;
        }
        tooltip.add(new TranslatableText("item.pitilesspillagers.musket.loaded"));
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

}