package com.daniel366cobra.pitilesspillagers.mixin;

import com.daniel366cobra.pitilesspillagers.ModEntities;
import com.daniel366cobra.pitilesspillagers.PitilessPillagers;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidAddRaiderMixin {

	@Shadow @Final private ServerWorld world;

	@Shadow protected abstract boolean addToWave(int wave, RaiderEntity entity);

	@Inject(at = @At("HEAD"), method = "addRaider")
	public void addRaiderCompanion(int wave, RaiderEntity raider, BlockPos pos, boolean existing, CallbackInfo ci){
		RaiderEntity raiderCompanion = null;

		boolean shouldReplace = raider.getRandom().nextBoolean();

		if (raider instanceof PillagerEntity) {
							raiderCompanion = ModEntities.ELITE_PILLAGER.create(world);
		}
		if (raider instanceof WitchEntity) {
				raiderCompanion = ModEntities.ARSONIST_PILLAGER.create(world);
		}
		if (raider instanceof VindicatorEntity) {
				raiderCompanion = ModEntities.KIDNAPPER_PILLAGER.create(world);
		}
		if (shouldReplace) {
			raider = raiderCompanion;
			raiderCompanion = null;
		}

		if (raiderCompanion != null) {

			boolean bl = addToWave(wave, raiderCompanion);
			if (bl) {
				raiderCompanion.setRaid((Raid)(Object)this);
				raiderCompanion.setWave(wave);
				raiderCompanion.setAbleToJoinRaid(true);
				raiderCompanion.setOutOfRaidCounter(0);
				if (!existing && pos != null) {
					raiderCompanion.setPosition((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5);
					raiderCompanion.initialize(this.world, this.world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
					raiderCompanion.addBonusForWave(wave, false);
					raiderCompanion.setOnGround(true);
					this.world.spawnEntityAndPassengers(raiderCompanion);
				}
			}
		}
	}

}



