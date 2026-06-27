package cc.sighs.extremeevasion.mixin;

import cc.sighs.extremeevasion.ExtremeEvasionEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin {

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
