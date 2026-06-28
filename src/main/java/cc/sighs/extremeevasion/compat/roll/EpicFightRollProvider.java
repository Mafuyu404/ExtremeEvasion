package cc.sighs.extremeevasion.compat.roll;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class EpicFightRollProvider implements RollProvider {
    private static final String CAPABILITIES_CLASS = "yesman.epicfight.world.capabilities.EpicFightCapabilities";
    private static final String DODGE_ANIMATION_CLASS = "yesman.epicfight.api.animation.types.DodgeAnimation";

    private Method getServerPlayerPatch;
    private Method getServerAnimator;
    private Field animationPlayerField;
    private Method isEnd;
    private Method getRealAnimation;
    private Method get;
    private Class<?> dodgeAnimationClass;

    @Override
    public String id() {
        return "epicfight";
    }

    @Override
    public void init() {
        try {
            Class<?> capabilitiesClass = Class.forName(CAPABILITIES_CLASS);
            dodgeAnimationClass = Class.forName(DODGE_ANIMATION_CLASS);
            getServerPlayerPatch = capabilitiesClass.getMethod("getServerPlayerPatch", ServerPlayer.class);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to initialize Epic Fight roll compatibility", exception);
        }
    }

    @Override
    public boolean isRolling(Player player) {
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
