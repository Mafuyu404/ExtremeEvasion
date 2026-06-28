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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
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
    private void extremeevasion$triggerBeforeMeleeDamage(LivingEntity target, CallbackInfo callbackInfo) {
        if (target instanceof ServerPlayer player
                && ExtremeEvasionEvents.hasActiveExtremeEvasionWindow(player)
                && ExtremeEvasionEvents.tryTriggerExtremeEvasion(player, mob.damageSources().mobAttack(mob))) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "canPerformAttack", at = @At("HEAD"), cancellable = true)
    private void extremeevasion$canPerformAttackAtRollStart(LivingEntity target, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (target instanceof Player player && ExtremeEvasionEvents.hasActiveExtremeEvasionWindow(player)) {
            Vec3 startPos = ExtremeEvasionEvents.getExtremeEvasionStartPos(player);
            callbackInfo.setReturnValue(mob.distanceToSqr(startPos.x, startPos.y, startPos.z) <= getAttackReachSqr(target));
        }
    }

    @Unique
    private double getAttackReachSqr(LivingEntity target) {
        return mob.getBbWidth() * 2.0F * mob.getBbWidth() * 2.0F + target.getBbWidth();
    }
}
