package dev.ftb.mods.ftblh.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.BiConsumer;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;

public class LittleHelperRenderer extends MobRenderer<LittleHelperEntity,LittleHelperModel> {
    // using the allay model for now
    private static final ResourceLocation HELPER_TEXTURE = new ResourceLocation("textures/entity/allay/allay.png");

    public LittleHelperRenderer(EntityRendererProvider.Context context) {
        super(context, new LittleHelperModel(context.bakeLayer(ModelLayers.ALLAY)), 0.4f);
    }

    @Override
    public void render(LittleHelperEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        super.render(entity, yaw, partialTick, poseStack, buffer, light);

        Component msg = entity.getActiveMsg();
        if (msg.getContents() != ComponentContents.EMPTY) {
            renderActiveMessage(entity, msg, poseStack, buffer);
        }
    }

    private void renderActiveMessage(LittleHelperEntity entity, Component msg, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.pushPose();
        poseStack.translate(0f, 0.8f, 0f);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.015F, -0.015F, 0.015F);

        Font font = getFont();
        int available = (int) (Minecraft.getInstance().getWindow().getGuiScaledWidth() * 0.3f);
        List<FormattedCharSequence> split = font.split(msg, available);

        int widestLine = split.stream().map(font::width).max(Integer::compare).orElse(0);

        drawTextBox(widestLine + 10, split.size() * font.lineHeight + 2, poseStack, buffer);

        for (int i = 0; i < split.size(); i++) {
            FormattedCharSequence line = split.get(i);
            float xOff = -font.width(line) / 2f;
            font.drawInBatch(line, xOff, (i - split.size() / 2f) * font.lineHeight, 0xFFFFFFFF, false, poseStack.last().pose(), buffer, Font.DisplayMode.SEE_THROUGH, 0, FULL_BRIGHT);
        }

        poseStack.popPose();
    }

    private void drawTextBox(int width, int height, PoseStack poseStack, MultiBufferSource buffer) {
        renderWithTypeAndFinish(poseStack, buffer, ModRenderTypes.UNTEXTURED_QUAD_NO_DEPTH, (posMat, builder) -> {
            float baseX = -width / 2f;
            float baseY = -height / 2f - 1;
            builder.vertex(posMat, baseX, baseY + height, 0.0F)
                    .color(0, 0, 0, 160)
                    .uv2(FULL_BRIGHT)
                    .endVertex();
            builder.vertex(posMat, baseX + width, baseY + height, 0.0F)
                    .color(0, 0, 0, 160)
                    .uv2(FULL_BRIGHT)
                    .endVertex();
            builder.vertex(posMat, baseX + width, baseY, 0.0F)
                    .color(0, 0, 0, 160)
                    .uv2(FULL_BRIGHT)
                    .endVertex();
            builder.vertex(posMat, baseX, baseY, 0.0F)
                    .color(0, 0, 0, 160)
                    .uv2(FULL_BRIGHT)
                    .endVertex();
        });
    }

    public static void renderWithTypeAndFinish(PoseStack matrixStack, MultiBufferSource buffer, RenderType type, BiConsumer<Matrix4f, VertexConsumer> consumer) {
        // use when drawing from RenderWorldLastEvent
        consumer.accept(matrixStack.last().pose(), buffer.getBuffer(type));
//        finishBuffer(buffer, type);
    }

    public static void finishBuffer(MultiBufferSource buffer, RenderType type) {
        if (buffer instanceof MultiBufferSource.BufferSource mbs) {
            RenderSystem.disableDepthTest();
            mbs.endBatch(type);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(LittleHelperEntity entity) {
        return HELPER_TEXTURE;
    }
}
