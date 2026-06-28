package cc.sighs.extremeevasion.compat.roll;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class CombatRollProvider implements RollProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String EVENT_CLASS = "net.combatroll.api.event.Event";
    private static final String EVENTS_CLASS = "net.combatroll.api.event.ServerSideRollEvents";
    private static final String START_EVENT_CLASS = "net.combatroll.api.event.ServerSideRollEvents$PlayerStartRolling";
    private static final String ROLL_MANAGER_CLASS = "net.combatroll.internals.RollManager";
    private static final int FALLBACK_ROLL_DURATION_TICKS = 8;

    private final Map<UUID, Long> rollingUntilTicks = new HashMap<>();

    @Override
    public String id() {
        return "combatroll";
    }

    @Override
    public void init() {
        try {
            Class<?> eventsClass = Class.forName(EVENTS_CLASS);
            Class<?> eventClass = Class.forName(EVENT_CLASS);
            Class<?> startEventClass = Class.forName(START_EVENT_CLASS);
            Field field = eventsClass.getField("PLAYER_START_ROLLING");
            Object event = field.get(null);
            Method register = eventClass.getMethod("register", Object.class);
            Object listener = Proxy.newProxyInstance(
                    startEventClass.getClassLoader(),
                    new Class<?>[]{startEventClass},
                    (proxy, method, args) -> {
                        if ("onPlayerStartedRolling".equals(method.getName())
                                && args != null
                                && args.length >= 1
                                && args[0] instanceof ServerPlayer player) {
                            markRolling(player);
                        }
                        return null;
                    }
            );
            register.invoke(event, listener);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            LOGGER.warn("Failed to initialize Combat Roll compatibility", exception);
        }
    }

    @Override
    public boolean isRolling(Player player) {
        Long rollingUntil = rollingUntilTicks.get(player.getUUID());
        if (rollingUntil == null) {
            return false;
        }

        long gameTime = player.level().getGameTime();
        if (gameTime <= rollingUntil) {
            return true;
        }

        rollingUntilTicks.remove(player.getUUID());
        return false;
    }

    private void markRolling(ServerPlayer player) {
        rollingUntilTicks.put(player.getUUID(), player.level().getGameTime() + rollDurationTicks());
    }

    private int rollDurationTicks() {
        try {
            Class<?> rollManagerClass = Class.forName(ROLL_MANAGER_CLASS);
            Method rollDuration = rollManagerClass.getMethod("rollDuration");
            Object value = rollDuration.invoke(null);
            if (value instanceof Integer ticks && ticks > 0) {
                return ticks;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        return FALLBACK_ROLL_DURATION_TICKS;
    }
}
