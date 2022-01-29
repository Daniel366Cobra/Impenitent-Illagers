package com.daniel366cobra.impenitent_illagers.mixin;

import com.daniel366cobra.impenitent_illagers.util.CustomRaidMemberEnum;
import com.daniel366cobra.impenitent_illagers.util.CustomRaidMembersHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.Random;

@Mixin(Raid.class)
public abstract class RaidAddRaiderMixin {

	@Shadow @Final private ServerWorld world;

	@Shadow private int wavesSpawned;

	@Shadow private float totalHealth;

	@Shadow protected abstract boolean isSpawningExtraWave();

	@Shadow @Final private Random random;

	@Shadow public abstract void setWaveCaptain(int wave, RaiderEntity entity);

	@Shadow public abstract void addRaider(int wave, RaiderEntity raider, @Nullable BlockPos pos, boolean existing);

	@Shadow public abstract int getMaxWaves(Difficulty difficulty);

	@Shadow private Optional<BlockPos> preCalculatedRavagerSpawnLocation;

	@Shadow public abstract void updateBar();

	@Shadow protected abstract void markDirty();

	@Shadow @Final private int waveCount;

	@Redirect(method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/village/raid/Raid;spawnNextWave(Lnet/minecraft/util/math/BlockPos;)V"))
	public void spawnNextWave(Raid instance, BlockPos pos){
		boolean hasCaptain = false;
		int nextWaveNumber = this.wavesSpawned + 1;
		this.totalHealth = 0.0f;
		LocalDifficulty localDifficulty = this.world.getLocalDifficulty(pos);
		boolean spawningExtraWave = this.isSpawningExtraWave();
		for (CustomRaidMemberEnum member : CustomRaidMemberEnum.values()) {
			int currentMemberCount = CustomRaidMembersHelper.getCount(member, nextWaveNumber, this.waveCount, spawningExtraWave) + CustomRaidMembersHelper.getBonusCount(member, this.random, nextWaveNumber, localDifficulty, spawningExtraWave);
			int k = 0;
			for (int l = 0; l < currentMemberCount; ++l) {
				RaiderEntity raiderEntity = member.type.create(this.world);
				if (!hasCaptain && raiderEntity != null) {
					if (raiderEntity.canLead()) {
						raiderEntity.setPatrolLeader(true);
						this.setWaveCaptain(nextWaveNumber, raiderEntity);
						hasCaptain = true;
					}
				}
				this.addRaider(nextWaveNumber, raiderEntity, pos, false);

				//Ravager rider selection
				if (member.type != EntityType.RAVAGER) continue;
				RaiderEntity ravagerRiderEntity = null;
				if (nextWaveNumber == this.getMaxWaves(Difficulty.NORMAL)) {
					ravagerRiderEntity = EntityType.PILLAGER.create(this.world);
				} else if (nextWaveNumber >= this.getMaxWaves(Difficulty.HARD)) {
					ravagerRiderEntity = k == 0 ? EntityType.EVOKER.create(this.world) : EntityType.VINDICATOR.create(this.world);
				}
				++k;
				if (ravagerRiderEntity == null) continue;
				this.addRaider(nextWaveNumber, ravagerRiderEntity, pos, false);
				ravagerRiderEntity.refreshPositionAndAngles(pos, 0.0f, 0.0f);
				ravagerRiderEntity.startRiding(raiderEntity);
			}
		}
		this.preCalculatedRavagerSpawnLocation = Optional.empty();
		++this.wavesSpawned;
		this.updateBar();
		this.markDirty();
	}



}



