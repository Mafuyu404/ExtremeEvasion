package cc.sighs.extremeevasion.compat.roll;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

final class EpicFightRollProvider implements RollProvider {
    @Override
    public String id() {
        return "epicfight";
    }

    @Override
    public boolean isRolling(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        return EpicFightRollProviderInner.isRolling(serverPlayer);
    }
}
