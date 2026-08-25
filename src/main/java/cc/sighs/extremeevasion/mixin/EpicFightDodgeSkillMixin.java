package cc.sighs.extremeevasion.mixin;

import cc.sighs.extremeevasion.ExtremeEvasionEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Mixin(value = DodgeSkill.class, remap = false)
public abstract class EpicFightDodgeSkillMixin {
    @Inject(method = "executeOnServer(Lyesman/epicfight/skill/SkillContainer;Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("HEAD"), remap = false)
    private void extremeevasion$startWindowOnEpicFightDodge(SkillContainer container, FriendlyByteBuf buffer, CallbackInfo callbackInfo) {
        ServerPlayerPatch patch = container.getServerExecutor();
        if (patch == null) {
            return;
        }

        ServerPlayer player = patch.getOriginal();
        ExtremeEvasionEvents.startExternalRollWindow(player);
    }
}
