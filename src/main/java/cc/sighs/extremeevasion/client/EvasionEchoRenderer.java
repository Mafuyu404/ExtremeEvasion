package cc.sighs.extremeevasion.client;

import cc.sighs.extremeevasion.ExtremeEvasion;
import cc.sighs.extremeevasion.entity.EvasionEchoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class EvasionEchoRenderer extends EntityRenderer<EvasionEchoEntity> {

    private static final ResourceLocation EMPTY_TEXTURE = ResourceLocation.fromNamespaceAndPath(ExtremeEvasion.MODID, "textures/entity/evasion_echo.png");

    public EvasionEchoRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EvasionEchoEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Intentionally left blank. The echo is invisible and server-authoritative only.
    }

    @Override
    public ResourceLocation getTextureLocation(EvasionEchoEntity entity) {
        return EMPTY_TEXTURE;
    }
}
