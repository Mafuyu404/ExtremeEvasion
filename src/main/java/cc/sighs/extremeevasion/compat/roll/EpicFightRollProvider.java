package cc.sighs.extremeevasion.compat.roll;

import cc.sighs.extremeevasion.ExtremeEvasionEvents;
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

final class EpicFightRollProvider implements RollProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CAPABILITIES_CLASS = "yesman.epicfight.world.capabilities.EpicFightCapabilities";
    private static final String EVENT_HOOKS_PLAYER_CLASS = "yesman.epicfight.api.event.EpicFightEventHooks$Player";
    private static final String EVENT_HOOK_CLASS = "yesman.epicfight.api.event.EventHook";
    private static final String DEFAULT_EVENT_SUBSCRIPTION_CLASS = "yesman.epicfight.api.event.subscription.DefaultEventSubscription";
    private static final String SKILL_CAST_EVENT_CLASS = "yesman.epicfight.api.event.types.player.SkillCastEvent";
    private static final String DODGE_SKILL_CLASS = "yesman.epicfight.skill.dodge.DodgeSkill";
    private static final String DODGE_ANIMATION_CLASS = "yesman.epicfight.api.animation.types.DodgeAnimation";
    private static final int FALLBACK_DODGE_DURATION_TICKS = 12;

    private final Map<UUID, Long> rollingUntilTicks = new HashMap<>();
    private Method getServerPlayerPatch;
    private Method getServerAnimator;
    private Method getSkillContainer;
    private Method isExecutable;
    private Method getSkill;
    private Method getPlayerPatch;
    private Method getOriginal;
    private Field animationPlayerField;
    private Method isEnd;
    private Method getRealAnimation;
    private Method get;
    private Class<?> dodgeSkillClass;
    private Class<?> dodgeAnimationClass;

    @Override
    public String id() {
        return "epicfight";
    }

    @Override
    public void init() {
        try {
            Class<?> capabilitiesClass = Class.forName(CAPABILITIES_CLASS);
            dodgeSkillClass = Class.forName(DODGE_SKILL_CLASS);
            dodgeAnimationClass = Class.forName(DODGE_ANIMATION_CLASS);
            getServerPlayerPatch = capabilitiesClass.getMethod("getServerPlayerPatch", ServerPlayer.class);
            registerSkillCastListener();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to initialize Epic Fight roll compatibility", exception);
        }
    }

    @Override
    public boolean isRolling(Player player) {
        if (isMarkedRolling(player)) {
            return true;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        try {
            Object patch = getServerPlayerPatch.invoke(null, serverPlayer);
            if (patch == null) {
                return false;
            }

            Object animator = getServerAnimator(patch);
            Object animationPlayer = getAnimationPlayer(animator);
            if (animationPlayer == null || isAnimationEnd(animationPlayer)) {
                return false;
            }

            Object realAnimationAccessor = getRealAnimation(animationPlayer);
            Object realAnimation = getAsset(realAnimationAccessor);
            return dodgeAnimationClass.isInstance(realAnimation);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return false;
        }
    }

    private void registerSkillCastListener() throws ReflectiveOperationException {
        Class<?> hooksClass = Class.forName(EVENT_HOOKS_PLAYER_CLASS);
        Class<?> eventHookClass = Class.forName(EVENT_HOOK_CLASS);
        Class<?> subscriptionClass = Class.forName(DEFAULT_EVENT_SUBSCRIPTION_CLASS);
        Class<?> skillCastEventClass = Class.forName(SKILL_CAST_EVENT_CLASS);

        Object castSkillHook = hooksClass.getField("CAST_SKILL").get(null);
        Method registerEvent = eventHookClass.getMethod("registerEvent", subscriptionClass, String.class);
        Object subscription = Proxy.newProxyInstance(
                subscriptionClass.getClassLoader(),
                new Class<?>[]{subscriptionClass},
                (proxy, method, args) -> {
                    if ("fire".equals(method.getName())
                            && args != null
                            && args.length == 1
                            && skillCastEventClass.isInstance(args[0])) {
                        onSkillCast(args[0]);
                    }
                    return null;
                }
        );

        registerEvent.invoke(castSkillHook, subscription, "extremeevasion:epicfight_dodge");
    }

    private void onSkillCast(Object event) {
        try {
            if (!isExecutable(event)) {
                return;
            }

            Object skillContainer = getSkillContainer(event);
            Object skill = getSkill(skillContainer);
            if (!dodgeSkillClass.isInstance(skill)) {
                return;
            }

            Object playerPatch = getPlayerPatch(event);
            Object original = getOriginal(playerPatch);
            if (original instanceof ServerPlayer player) {
                markRolling(player);
                ExtremeEvasionEvents.startExternalRollWindow(player);
            }
        } catch (ReflectiveOperationException | RuntimeException exception) {
            LOGGER.debug("Failed to handle Epic Fight dodge cast", exception);
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

    private void markRolling(ServerPlayer player) {
        rollingUntilTicks.put(player.getUUID(), player.level().getGameTime() + FALLBACK_DODGE_DURATION_TICKS);
    }

    private Object getSkillContainer(Object event) throws ReflectiveOperationException {
        if (getSkillContainer == null) {
            getSkillContainer = event.getClass().getMethod("getSkillContainer");
        }
        return getSkillContainer.invoke(event);
    }

    private boolean isExecutable(Object event) throws ReflectiveOperationException {
        if (isExecutable == null) {
            isExecutable = event.getClass().getMethod("isExecutable");
        }
        return Boolean.TRUE.equals(isExecutable.invoke(event));
    }

    private Object getSkill(Object skillContainer) throws ReflectiveOperationException {
        if (getSkill == null) {
            getSkill = skillContainer.getClass().getMethod("getSkill");
        }
        return getSkill.invoke(skillContainer);
    }

    private Object getPlayerPatch(Object event) throws ReflectiveOperationException {
        if (getPlayerPatch == null) {
            getPlayerPatch = event.getClass().getMethod("getPlayerPatch");
        }
        return getPlayerPatch.invoke(event);
    }

    private Object getOriginal(Object patch) throws ReflectiveOperationException {
        if (getOriginal == null) {
            getOriginal = patch.getClass().getMethod("getOriginal");
        }
        return getOriginal.invoke(patch);
    }

    private Object getServerAnimator(Object patch) throws ReflectiveOperationException {
        if (getServerAnimator == null) {
            getServerAnimator = patch.getClass().getMethod("getServerAnimator");
        }
        return getServerAnimator.invoke(patch);
    }

    private Object getAnimationPlayer(Object animator) throws ReflectiveOperationException {
        if (animationPlayerField == null) {
            animationPlayerField = animator.getClass().getField("animationPlayer");
        }
        return animationPlayerField.get(animator);
    }

    private boolean isAnimationEnd(Object animationPlayer) throws ReflectiveOperationException {
        if (isEnd == null) {
            isEnd = animationPlayer.getClass().getMethod("isEnd");
        }
        return Boolean.TRUE.equals(isEnd.invoke(animationPlayer));
    }

    private Object getRealAnimation(Object animationPlayer) throws ReflectiveOperationException {
        if (getRealAnimation == null) {
            getRealAnimation = animationPlayer.getClass().getMethod("getRealAnimation");
        }
        return getRealAnimation.invoke(animationPlayer);
    }

    private Object getAsset(Object accessor) throws ReflectiveOperationException {
        if (get == null) {
            get = accessor.getClass().getMethod("get");
        }
        return get.invoke(accessor);
    }
}
