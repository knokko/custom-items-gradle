package nl.knokko.customitems.plugin.tasks.miningspeed;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import static nl.knokko.customitems.plugin.tasks.miningspeed.MiningSpeedManager.EFFECT_DURATION;
import static nl.knokko.customitems.plugin.tasks.miningspeed.MiningSpeedManager.UPDATE_PERIOD;

class MiningSpeedEntry {

    private final Player player;
    private final Block block;
    private final int speed;

    private final PotionEffect oldEffect;
    private int remainingOldDuration;

    MiningSpeedEntry(Player player, Block block, int speed) {
        if (speed == 0) throw new IllegalArgumentException("Speed should not be 0 (because it would be useless)");

        this.player = player;
        this.block = block;
        this.speed = speed;

        if (speed > 0) {
            this.oldEffect = player.getPotionEffect(PotionEffectType.FAST_DIGGING);
        } else {
            this.oldEffect = player.getPotionEffect(PotionEffectType.SLOW_DIGGING);
        }

        if (this.oldEffect != null) {
            this.remainingOldDuration = this.oldEffect.getDuration();
        }

        this.prolongEffect();
    }

    private void prolongEffect() {
        if (speed > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, EFFECT_DURATION, speed - 1), true);
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, EFFECT_DURATION, -speed - 1), true);
        }
    }

    void stopEffect() {
        if (speed > 0) {
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        } else {
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        }

        if (oldEffect != null && remainingOldDuration > 0) {
            player.addPotionEffect(new PotionEffect(
                    oldEffect.getType(), remainingOldDuration, oldEffect.getAmplifier(),
                    oldEffect.isAmbient(), oldEffect.hasParticles(), oldEffect.hasIcon())
            );
        }
    }

    private boolean isStillBreakingTheSameBlock() {
        if (!player.isOnline()) return false;

        RayTraceResult rayTraceResult = player.rayTraceBlocks(7);
        if (rayTraceResult == null) return false;
        if (rayTraceResult.getHitBlock() == null) return false;
        return rayTraceResult.getHitBlock().equals(this.block);
    }

    boolean update() {
        if (oldEffect != null) remainingOldDuration -= UPDATE_PERIOD;

        if (isStillBreakingTheSameBlock()) {
            prolongEffect();
            return false;
        } else {
            stopEffect();
            return true;
        }
    }
}
