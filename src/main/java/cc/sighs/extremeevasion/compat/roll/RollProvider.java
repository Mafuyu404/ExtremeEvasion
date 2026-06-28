package cc.sighs.extremeevasion.compat.roll;

import net.minecraft.world.entity.player.Player;

interface RollProvider {
    String id();

    default void init() {
    }

    boolean isRolling(Player player);
}
