package cc.sighs.extremeevasion.mixin;

import cc.sighs.extremeevasion.ExtremeEvasionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin {

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Inject(
            method = "checkAndPerformAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/PathfinderMob;doHurtTarget(Lnet/minecraft/world/entity/Entity;)Z"
            ),
            cancellable = true
    )
    private void extremeevasion$triggerBeforeMeleeDamage(LivingEntity target, double distanceSqr, CallbackInfo callbackInfo) {
        if (target instanceof ServerPlayer player
                && ExtremeEvasionEvents.hasActiveExtremeEvasionWindow(player)
                && ExtremeEvasionEvents.tryTriggerExtremeEvasion(player, mob.damageSources().mobAttack(mob))) {
            callbackInfo.cancel();
        }
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/PathfinderMob;getPerceivedTargetDistanceSquareForMeleeAttack(Lnet/minecraft/world/entity/LivingEntity;)D"
            )
    )
    private double extremeevasion$useRollStartDistance(PathfinderMob mob, LivingEntity target) {
        if (target instanceof Player player && ExtremeEvasionEvents.hasActiveExtremeEvasionWindow(player)) {
            Vec3 startPos = ExtremeEvasionEvents.getExtremeEvasionStartPos(player);
            return mob.distanceToSqr(startPos.x, startPos.y, startPos.z);
        }
        return mob.getPerceivedTargetDistanceSquareForMeleeAttack(target);
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getX()D"
            )
    )
    private double extremeevasion$useRollStartX(LivingEntity target) {
        if (target instanceof Player player && ExtremeEvasionEvents.hasActiveExtremeEvasionWindow(player)) {
            return ExtremeEvasionEvents.getExtremeEvasionStartPos(player).x;
        }
        return target.getX();
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getY()D"
            )
    )
    private double extremeevasion$useRollStartY(LivingEntity target) {
        if (target instanceof Player player && ExtremeEvasionEvents.hasActiveExtremeEvasionWindow(player)) {
            Vec3 startPos = ExtremeEvasionEvents.getExtremeEvasionStartPos(player);
            return startPos.y;
        }
        return target.getY();
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D"
            )
    )
    private double extremeevasion$useRollStartZ(LivingEntity target) {
        if (target instanceof Player player && ExtremeEvasionEvents.hasActiveExtremeEvasionWindow(player)) {
            return ExtremeEvasionEvents.getExtremeEvasionStartPos(player).z;
        }
        return target.getZ();
    }
}
