package cc.sighs.extremeevasion.compat.roll;

import cc.sighs.extremeevasion.ExtremeEvasionEvents;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class ParCoolRollProvider implements RollProvider {
    private static final String PARKOURABILITY_CLASS = "com.alrex.parcool.common.attachment.common.Parkourability";
    private static final String ROLL_CLASS = "com.alrex.parcool.common.action.impl.Roll";
    private static final String DODGE_CLASS = "com.alrex.parcool.common.action.impl.Dodge";
    private static final int FALLBACK_ROLL_DURATION_TICKS = 12;

    private final Map<UUID, Long> rollingUntilTicks = new HashMap<>();
    private Method getParkourability;
    private Method isDoingAny;
    private Class<?> rollClass;
    private Class<?> dodgeClass;

    @Override
    public String id() {
        return "parcool";
    }

    @Override
    public void init() {
        try {
            Class<?> parkourabilityClass = Class.forName(PARKOURABILITY_CLASS);
            rollClass = Class.forName(ROLL_CLASS);
            dodgeClass = Class.forName(DODGE_CLASS);
            getParkourability = parkourabilityClass.getMethod("get", Player.class);
            isDoingAny = parkourabilityClass.getMethod("isDoingAny", Class[].class);
            NeoForge.EVENT_BUS.register(this);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to initialize ParCool roll compatibility", exception);
        }
    }

    @Override
    public boolean isRolling(Player player) {
        if (isMarkedRolling(player)) {
            return true;
        }

        try {
            Object parkourability = getParkourability.invoke(null, player);
            return parkourability != null
                    && Boolean.TRUE.equals(isDoingAny.invoke(parkourability, (Object) new Class<?>[]{rollClass, dodgeClass}));
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return false;
        }
    }

    @SubscribeEvent
    public void onParCoolActionStart(ParCoolActionEvent.Start.Post event) {
        Player player = event.getPlayer();
        Object action = event.getAction();
        if (player != null && isRollOrDodge(action)) {
            rollingUntilTicks.put(player.getUUID(), player.level().getGameTime() + FALLBACK_ROLL_DURATION_TICKS);
            ExtremeEvasionEvents.startExternalRollWindow(player);
        }
    }

    private boolean isMarkedRolling(Player player) {
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

    private boolean isRollOrDodge(Object action) {
        return rollClass.isInstance(action) || dodgeClass.isInstance(action);
    }
}
