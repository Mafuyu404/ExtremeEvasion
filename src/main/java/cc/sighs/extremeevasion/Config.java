package cc.sighs.extremeevasion;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ExtremeEvasion.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue EXTREME_COUNTER_ATTACK_WINDOW_TICKS = BUILDER
            .comment(
                    "Ticks after a successful extreme evasion during which Extreme Counter is available.",
                    "极限闪避成功后，极限反击可用的持续 tick 数。"
            )
            .defineInRange("extremeCounterAttackWindowTicks", 30, 0, 20 * 60);

    private static final ForgeConfigSpec.IntValue EXTREME_COUNTER_ATTACK_CHARGES = BUILDER
            .comment(
                    "Number of attacks empowered by Extreme Counter after one successful extreme evasion.",
                    "每次极限闪避成功后，极限反击可强化的攻击次数。"
            )
            .defineInRange("extremeCounterAttackCharges", 1, 0, 100);

    private static final ForgeConfigSpec.BooleanValue ENABLE_EXTREME_COUNTER_ATTACK_CRITICAL = BUILDER
            .comment(
                    "Whether Extreme Counter forces empowered attacks to become critical hits.",
                    "极限反击是否让被强化的攻击必定暴击。"
            )
            .define("enableExtremeCounterAttackCritical", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_EXTREME_COUNTER_ATTACK_ARMOR_PIERCING = BUILDER
            .comment(
                    "Whether Extreme Counter makes empowered attacks ignore armor.",
                    "极限反击是否让被强化的攻击无视护甲。"
            )
            .define("enableExtremeCounterAttackArmorPiercing", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_EXTREME_COUNTER_GOLD_SHADER = BUILDER
            .comment(
                    "Whether to render the gold shader glow on the held item while Extreme Counter is active.",
                    "极限反击激活期间，是否给手持物品渲染金色发光着色器。"
            )
            .define("enableExtremeCounterGoldShader", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_BULLET_TIME = BUILDER
            .comment(
                    "Whether successful extreme evasion triggers bullet time in singleplayer.",
                    "单人游玩时，极限闪避成功是否触发子弹时间。"
            )
            .define("enableBulletTime", true);

    private static final ForgeConfigSpec.DoubleValue BULLET_TIME_SPEED = BUILDER
            .comment(
                    "World time speed during bullet time. 0.2 means 20 percent speed.",
                    "子弹时间期间的世界时间流速。0.2 表示 20% 速度。"
            )
            .defineInRange("bulletTimeSpeed", 0.3D, 0.01D, 1.0D);

    private static final ForgeConfigSpec.IntValue BULLET_TIME_DURATION_MILLIS = BUILDER
            .comment(
                    "Real-time bullet time duration in milliseconds.",
                    "子弹时间持续的真实时间，单位为毫秒。"
            )
            .defineInRange("bulletTimeDurationMillis", 3000, 0, 60000);

    private static final ForgeConfigSpec.IntValue BULLET_TIME_VISUAL_FADE_MILLIS = BUILDER
            .comment(
                    "Real-time fade in/out duration in milliseconds for the bullet-time screen filter.",
                    "子弹时间屏幕滤镜淡入/淡出的真实时间，单位为毫秒。"
            )
            .defineInRange("bulletTimeVisualFadeMillis", 250, 0, 10000);

    private static final ForgeConfigSpec.BooleanValue ENABLE_BULLET_TIME_SCREEN_SHADER = BUILDER
            .comment(
                    "Whether to render the blue-gray bullet-time screen shader.",
                    "是否渲染子弹时间的蓝灰屏幕着色器。"
            )
            .define("enableBulletTimeScreenShader", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_BULLET_TIME_TRIGGER_SOUND = BUILDER
            .comment(
                    "Whether to play the bullet-time trigger sound.",
                    "是否播放子弹时间触发音效。"
            )
            .define("enableBulletTimeTriggerSound", true);

    private static final ForgeConfigSpec.DoubleValue BULLET_TIME_TRIGGER_SOUND_VOLUME = BUILDER
            .comment(
                    "Volume of the bullet-time trigger sound.",
                    "子弹时间触发音效的音量。"
            )
            .defineInRange("bulletTimeTriggerSoundVolume", 0.65D, 0.0D, 4.0D);

    private static final ForgeConfigSpec.DoubleValue BULLET_TIME_TRIGGER_SOUND_PITCH = BUILDER
            .comment(
                    "Pitch of the bullet-time trigger sound.",
                    "子弹时间触发音效的音高。"
            )
            .defineInRange("bulletTimeTriggerSoundPitch", 1.0D, 0.1D, 4.0D);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int extremeCounterAttackWindowTicks;
    public static int extremeCounterAttackCharges;
    public static boolean enableExtremeCounterAttackCritical;
    public static boolean enableExtremeCounterAttackArmorPiercing;
    public static boolean enableExtremeCounterGoldShader;
    public static boolean enableBulletTime;
    public static double bulletTimeSpeed;
    public static int bulletTimeDurationMillis;
    public static int bulletTimeVisualFadeMillis;
    public static boolean enableBulletTimeScreenShader;
    public static boolean enableBulletTimeTriggerSound;
    public static float bulletTimeTriggerSoundVolume;
    public static float bulletTimeTriggerSoundPitch;

    private Config() {
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        extremeCounterAttackWindowTicks = EXTREME_COUNTER_ATTACK_WINDOW_TICKS.get();
        extremeCounterAttackCharges = EXTREME_COUNTER_ATTACK_CHARGES.get();
        enableExtremeCounterAttackCritical = ENABLE_EXTREME_COUNTER_ATTACK_CRITICAL.get();
        enableExtremeCounterAttackArmorPiercing = ENABLE_EXTREME_COUNTER_ATTACK_ARMOR_PIERCING.get();
        enableExtremeCounterGoldShader = ENABLE_EXTREME_COUNTER_GOLD_SHADER.get();
        enableBulletTime = ENABLE_BULLET_TIME.get();
        bulletTimeSpeed = BULLET_TIME_SPEED.get();
        bulletTimeDurationMillis = BULLET_TIME_DURATION_MILLIS.get();
        bulletTimeVisualFadeMillis = BULLET_TIME_VISUAL_FADE_MILLIS.get();
        enableBulletTimeScreenShader = ENABLE_BULLET_TIME_SCREEN_SHADER.get();
        enableBulletTimeTriggerSound = ENABLE_BULLET_TIME_TRIGGER_SOUND.get();
        bulletTimeTriggerSoundVolume = BULLET_TIME_TRIGGER_SOUND_VOLUME.get().floatValue();
        bulletTimeTriggerSoundPitch = BULLET_TIME_TRIGGER_SOUND_PITCH.get().floatValue();
    }
}
