package com.daniel366cobra.pitilesspillagers.entity;

import com.daniel366cobra.pitilesspillagers.ModItems;
import com.daniel366cobra.pitilesspillagers.PitilessPillagers;
import com.daniel366cobra.pitilesspillagers.entity.mob.InterloperPillagerEntity;
import com.daniel366cobra.pitilesspillagers.item.MusketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.EnumSet;

import static com.daniel366cobra.pitilesspillagers.PitilessPillagers.LOGGER;

public class MusketAttackGoal<T extends InterloperPillagerEntity & MusketUser>
        extends Goal {
    public static final UniformIntProvider COOLDOWN_RANGE = TimeHelper.betweenSeconds(1, 2);
    private final T actor;
    private Stage stage = Stage.UNLOADED;
    private final double speed;
    private final float squaredRange;
    private int seeingTargetTicker;
    private int aimingTicksLeft;
    private int postHurtTicksLeft;
    private int cooldown;

    public MusketAttackGoal(T actor, double speed, float range) {
        this.actor = actor;
        this.speed = speed;
        this.squaredRange = range * range;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return this.hasAliveTarget() && this.isEntityHoldingMusket();
    }

    private boolean isEntityHoldingMusket() {
        return this.actor.isHolding(ModItems.MUSKET);
    }

    @Override
    public boolean shouldContinue() {
        return this.hasAliveTarget() && (this.canStart() || !this.actor.getNavigation().isIdle()) && this.isEntityHoldingMusket();
    }

    private boolean hasAliveTarget() {
        return this.actor.getTarget() != null && this.actor.getTarget().isAlive();
    }

    @Override
    public void stop() {
        super.stop();
        this.actor.setAiming(false);
        this.actor.setAttacking(false);
        this.actor.setTarget(null);
        this.seeingTargetTicker = 0;
        if (this.actor.isUsingItem()) {
            this.actor.clearActiveItem();
            this.actor.setLoading(false);
            MusketItem.setLoaded(this.actor.getActiveItem(), false);
        }
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity targetEntity = this.actor.getTarget();
        if (targetEntity == null) {
            return;
        }

        if(this.actor.hurtTime > 0) {
            this.postHurtTicksLeft = 10 + this.actor.getRandom().nextInt(10);
        }

        if (this.postHurtTicksLeft > 0) {
            --this.postHurtTicksLeft;
        }

        boolean canSeeTarget = this.actor.getVisibilityCache().canSee(targetEntity);
        boolean seeingTargetTickerPositive = this.seeingTargetTicker > 0;
        //Cannot see target, but ticker is positive -> set ticker to 0
        if (canSeeTarget != seeingTargetTickerPositive) {
            this.seeingTargetTicker = 0;
        }
        if (canSeeTarget) {
            ++this.seeingTargetTicker;
        } else {
            --this.seeingTargetTicker;
        }

        double squaredDistanceToTarget = this.actor.squaredDistanceTo(targetEntity);
        //"Ready to attack, but target not in range or not clearly locked"
        boolean targetNotLocked = (squaredDistanceToTarget > (double)this.squaredRange || this.seeingTargetTicker < 5) && this.aimingTicksLeft == 0;
        if (targetNotLocked) {
            --this.cooldown;
            if (this.cooldown <= 0) {
                //Navigating to target
                this.actor.setAiming(false);
                this.actor.getNavigation().startMovingTo(targetEntity, this.isUnloaded()? this.speed * 0.8 : this.speed);
                this.cooldown = COOLDOWN_RANGE.get(this.actor.getRandom());
            }
        } else {
            //Target in range
            this.cooldown = 0;
            this.actor.getNavigation().stop();
        }
        this.actor.getLookControl().lookAt(targetEntity, 15.0f, 15.0f);

        if (this.postHurtTicksLeft == 0) {
            switch (this.stage) {
                case UNLOADED: {
                    if (!targetNotLocked) {
                        //Loading weapon
                        LOGGER.info("Set weapon to load");
                        this.actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, ModItems.MUSKET));
                        this.stage = Stage.LOADING;
                        this.actor.setLoading(true);
                    }
                    break;
                }
                case LOADING: {
                    if (!this.actor.isUsingItem()) {
                        this.stage = Stage.UNLOADED;
                    }
                    if ((this.actor.getItemUseTime()) >= MusketItem.getLoadTime(this.actor.getActiveItem())) {
                        //Loaded weapon, started aiming
                        LOGGER.info("Weapon loaded");
                        this.actor.stopUsingItem();
                        this.stage = Stage.LOADED;
                        this.aimingTicksLeft = 30 + this.actor.getRandom().nextInt(10);
                        this.actor.setLoading(false);
                    }

                    break;
                }
                case LOADED: {
                    LOGGER.info("Aiming weapon");
                    this.actor.setAiming(true);
                    --this.aimingTicksLeft;

                    if (this.aimingTicksLeft == 0) {
                        this.stage = Stage.READY_TO_ATTACK;
                    }

                    break;
                }
                case READY_TO_ATTACK: {
                    if (canSeeTarget) {
                        LOGGER.info("Attacking target");
                        //Attacking target
                        this.actor.attack(targetEntity);
                        this.actor.setAiming(false);
                        ItemStack i = this.actor.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this.actor, ModItems.MUSKET));
                        MusketItem.setLoaded(i, false);
                        this.stage = Stage.UNLOADED;
                    }
                }
                default:
                    break;
            }
        }
    }

    private boolean isUnloaded() {
        return this.stage == Stage.UNLOADED;
    }

    enum Stage {
        UNLOADED,
        LOADING,
        LOADED,
        READY_TO_ATTACK;

    }
}
