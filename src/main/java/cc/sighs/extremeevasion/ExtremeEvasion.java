package cc.sighs.extremeevasion;

import cc.sighs.extremeevasion.client.EvasionEchoRenderer;
import cc.sighs.extremeevasion.client.CounterAttackGoldRenderType;
import cc.sighs.extremeevasion.entity.EvasionEchoEntity;
import cc.sighs.extremeevasion.network.ExtremeEvasionNetwork;
import com.mojang.logging.LogUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExtremeEvasion.MODID)
public class ExtremeEvasion {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "extremeevasion";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<EntityType<EvasionEchoEntity>> EVASION_ECHO = ENTITY_TYPES.register("evasion_echo",
            () -> EntityType.Builder.<EvasionEchoEntity>of(EvasionEchoEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(4)
                    .updateInterval(1)
                    .build("evasion_echo"));

    public ExtremeEvasion() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ExtremeEvasionNetwork.register();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ENTITY_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::registerEntityAttributes);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(EVASION_ECHO.get(), AttributeSupplier.builder()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .build());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EVASION_ECHO.get(), EvasionEchoRenderer::new);
        }

        @SubscribeEvent
        public static void registerShaders(RegisterShadersEvent event) throws IOException {
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), new ResourceLocation(MODID, "counter_attack_gold"), DefaultVertexFormat.NEW_ENTITY),
                    CounterAttackGoldRenderType::setGoldOverlayShader
            );
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), new ResourceLocation(MODID, "counter_attack_halo"), DefaultVertexFormat.NEW_ENTITY),
                    CounterAttackGoldRenderType::setGoldHaloShader
            );
        }
    }
}
