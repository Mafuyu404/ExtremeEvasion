package cc.sighs.extremeevasion.compat.roll;

import net.minecraft.world.entity.player.Player;

final class MovesLikeMafuyuRollProvider implements RollProvider {
    private static final String ROLL_TAG = "roll";

    @Override
    public String id() {
        return "moveslikemafuyu";
    }

    @Override
    public boolean isRolling(Player player) {
        return player.getTags().contains(ROLL_TAG);
    }
}
