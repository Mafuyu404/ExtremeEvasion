package cc.sighs.extremeevasion;

import cc.sighs.extremeevasion.client.EvasionEchoRenderer;
import cc.sighs.extremeevasion.client.CounterAttackGoldRenderType;
import cc.sighs.extremeevasion.client.BulletTimeShaderManager;
import cc.sighs.extremeevasion.compat.roll.RollCompat;
import cc.sighs.extremeevasion.entity.EvasionEchoEntity;
import cc.sighs.extremeevasion.network.ExtremeEvasionNetwork;
import com.mojang.logging.LogUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.io.IOException;

// The value here should match the mod id in the NeoForge metadata.
@Mod(ExtremeEvasion.MODID)
public class ExtremeEvasion {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "extremeevasion";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EvasionEchoEntity>> EVASION_ECHO = ENTITY_TYPES.register("evasion_echo",
            () -> EntityType.Builder.<EvasionEchoEntity>of(EvasionEchoEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(4)
                    .updateInterval(1)
                    .build("evasion_echo"));

    public ExtremeEvasion(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ExtremeEvasionNetwork::register);
        modEventBus.addListener(Config::onLoad);

        ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerEntityAttributes);
        NeoForge.EVENT_BUS.register(ExtremeEvasionEvents.class);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientModEvents::registerEntityRenderers);
            modEventBus.addListener(ClientModEvents::registerShadersUnchecked);
            modEventBus.addListener(BulletTimeShaderManager::onRegisterClientReloadListeners);
            NeoForge.EVENT_BUS.addListener(BulletTimeShaderManager::onRenderLevelStage);
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(RollCompat::init);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(EVASION_ECHO.get(), ArmorStand.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .build());
    }

    public static class ClientModEvents {

        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EVASION_ECHO.get(), EvasionEchoRenderer::new);
        }

        public static void registerShadersUnchecked(RegisterShadersEvent event) {
            try {
                registerShaders(event);
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to register Extreme Evasion shaders", exception);
            }
        }

        public static void registerShaders(RegisterShadersEvent event) throws IOException {
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(MODID, "counter_attack_gold"), DefaultVertexFormat.NEW_ENTITY),
                    CounterAttackGoldRenderType::setGoldOverlayShader
            );
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(MODID, "counter_attack_halo"), DefaultVertexFormat.NEW_ENTITY),
                    CounterAttackGoldRenderType::setGoldHaloShader
            );
        }
    }
}
