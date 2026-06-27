package cc.sighs.extremeevasion;

import cc.sighs.extremeevasion.entity.EvasionEchoEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtremeEvasion.MODID)
public final class ExtremeEvasionEvents {

    private static final String ROLL_TAG = "roll";
    private static final String DATA_ROOT = ExtremeEvasion.MODID;
    private static final String DATA_ACTIVE = "extreme_window_active";
    private static final String DATA_END_TICK = "extreme_window_end_tick";
    private static final String DATA_SUCCESS = "extreme_window_success";
    private static final String DATA_START_X = "extreme_window_start_x";
    private static final String DATA_START_Y = "extreme_window_start_y";
    private static final String DATA_START_Z = "extreme_window_start_z";
    private static final String DATA_ECHO_UUID = "extreme_window_echo_uuid";
    private static final int EXTREME_EVASION_WINDOW_TICKS = 6;
    private static final int STRENGTH_DURATION_TICKS = 100;
    private static final int STRENGTH_AMPLIFIER = 0;

    private ExtremeEvasionEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }

        Player player = event.player;
        CompoundTag data = getData(player);
        boolean rolling = player.getTags().contains(ROLL_TAG);
        boolean active = data.getBoolean(DATA_ACTIVE);

        if (rolling && !active) {
            startWindow(player, data);
            return;
        }

        if (!active) {
            return;
        }

        long gameTime = player.level().getGameTime();
        if (!rolling || gameTime > data.getLong(DATA_END_TICK) || data.getBoolean(DATA_SUCCESS)) {
            endWindow(player, data);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof Mob attacker)) {
            return;
        }

        if (event.getSource().getDirectEntity() != attacker) {
            return;
        }

        CompoundTag data = getData(player);
        if (!data.getBoolean(DATA_ACTIVE) || data.getBoolean(DATA_SUCCESS)) {
            return;
        }

        long gameTime = player.level().getGameTime();
        if (gameTime > data.getLong(DATA_END_TICK) || !player.getTags().contains(ROLL_TAG)) {
            endWindow(player, data);
            return;
        }

        if (tryTriggerExtremeEvasion(player)) {
            event.setAmount(0.0F);
            event.setCanceled(true);
        }
    }

    private static void startWindow(Player player, CompoundTag data) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        removeEcho(serverPlayer, data);
        data.putBoolean(DATA_ACTIVE, true);
        data.putBoolean(DATA_SUCCESS, false);
        data.putLong(DATA_END_TICK, player.level().getGameTime() + EXTREME_EVASION_WINDOW_TICKS);
        data.putDouble(DATA_START_X, player.getX());
        data.putDouble(DATA_START_Y, player.getY());
        data.putDouble(DATA_START_Z, player.getZ());
        spawnEcho(serverPlayer, data);
    }

    private static void endWindow(Player player, CompoundTag data) {
        if (player instanceof ServerPlayer serverPlayer) {
            removeEcho(serverPlayer, data);
        }
        clearWindow(data);
    }

    private static void clearWindow(CompoundTag data) {
        // The actual echo cleanup is handled before calling this helper.
        data.remove(DATA_ACTIVE);
        data.remove(DATA_END_TICK);
        data.remove(DATA_SUCCESS);
        data.remove(DATA_START_X);
        data.remove(DATA_START_Y);
        data.remove(DATA_START_Z);
        data.remove(DATA_ECHO_UUID);
    }

    private static CompoundTag getData(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(DATA_ROOT, CompoundTag.TAG_COMPOUND)) {
            persistentData.put(DATA_ROOT, new CompoundTag());
        }
        return persistentData.getCompound(DATA_ROOT);
    }

    public static boolean hasActiveExtremeEvasionWindow(Player player) {
        CompoundTag data = getData(player);
        return data.getBoolean(DATA_ACTIVE)
                && !data.getBoolean(DATA_SUCCESS)
                && player.getTags().contains(ROLL_TAG)
                && player.level().getGameTime() <= data.getLong(DATA_END_TICK);
    }

    public static Vec3 getExtremeEvasionStartPos(Player player) {
        CompoundTag data = getData(player);
        return new Vec3(
                data.getDouble(DATA_START_X),
                data.getDouble(DATA_START_Y),
                data.getDouble(DATA_START_Z)
        );
    }

    public static boolean tryTriggerExtremeEvasion(ServerPlayer player) {
        CompoundTag data = getData(player);
        if (!hasActiveExtremeEvasionWindow(player)) {
            return false;
        }

        data.putBoolean(DATA_SUCCESS, true);
        removeEcho(player, data);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, STRENGTH_DURATION_TICKS, STRENGTH_AMPLIFIER));
        return true;
    }

    private static void spawnEcho(ServerPlayer player, CompoundTag data) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        EvasionEchoEntity echo = ExtremeEvasion.EVASION_ECHO.get().create(serverLevel);
        if (echo == null) {
            return;
        }

        echo.setOwner(player);
        echo.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        echo.setYBodyRot(player.getYRot());
        echo.setYHeadRot(player.getYRot());
        serverLevel.addFreshEntity(echo);
        data.putUUID(DATA_ECHO_UUID, echo.getUUID());
    }

    private static void removeEcho(ServerPlayer player, CompoundTag data) {
        if (!data.hasUUID(DATA_ECHO_UUID) || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (serverLevel.getEntity(data.getUUID(DATA_ECHO_UUID)) instanceof EvasionEchoEntity echo) {
            echo.discard();
        }
        data.remove(DATA_ECHO_UUID);
    }
}
