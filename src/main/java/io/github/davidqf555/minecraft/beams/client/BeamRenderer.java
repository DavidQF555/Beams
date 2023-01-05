package io.github.davidqf555.minecraft.beams.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class BeamRenderer<T extends BeamEntity> extends EntityRenderer<T> {

    public BeamRenderer(EntityRendererProvider.Context manager) {
        super(manager);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        Vec3 dif = entityIn.getEnd().subtract(entityIn.position());
        double startHeightRadius = entityIn.getStartHeight() / 2;
        double length = dif.length();
        Vector3f vertex = new Vector3f(0, 0, (float) length);
        matrixStackIn.pushPose();
        float yaw = (float) (Math.PI / 2 - Mth.atan2(-dif.z(), dif.x()));
        float pitch = (float) Math.asin(Mth.clamp(dif.y() / length, -1, 1));
        matrixStackIn.mulPose(Vector3f.YN.rotation(yaw));
        matrixStackIn.mulPose(Vector3f.XP.rotation(pitch));
        VertexConsumer builder = bufferIn.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = matrixStackIn.last().pose();
        int color = entityIn.getColor();
        float alpha = FastColor.ARGB32.alpha(color) / 255f;
        float red = FastColor.ARGB32.red(color) / 255f;
        float green = FastColor.ARGB32.green(color) / 255f;
        float blue = FastColor.ARGB32.blue(color) / 255f;
        int layers = entityIn.getLayers();
        double startWidthRadius = entityIn.getStartWidth() / 2;
        double endWidthRadius = entityIn.getEndWidth() / 2;
        double endHeightRadius = entityIn.getEndHeight() / 2;
        for (int i = 1; i <= layers; i++) {
            float sWidthRadius = (float) (startWidthRadius * i / layers);
            float sHeightRadius = (float) (startHeightRadius * i / layers);
            float eWidthRadius = (float) (endWidthRadius * i / layers);
            float eHeightRadius = (float) (endHeightRadius * i / layers);

            builder.vertex(matrix4f, -eWidthRadius, -eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - sWidthRadius, -vertex.y() - sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + sWidthRadius, -vertex.y() - sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, eWidthRadius, -eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, eWidthRadius, -eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + sWidthRadius, -vertex.y() - sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - sWidthRadius, -vertex.y() - sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -eWidthRadius, -eHeightRadius, 0).color(red, green, blue, alpha).endVertex();

            builder.vertex(matrix4f, eWidthRadius, -eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + sWidthRadius, -vertex.y() - sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + sWidthRadius, -vertex.y() + sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, eWidthRadius, eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, eWidthRadius, eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + sWidthRadius, -vertex.y() + sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + sWidthRadius, -vertex.y() - sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, eWidthRadius, -eHeightRadius, 0).color(red, green, blue, alpha).endVertex();

            builder.vertex(matrix4f, eWidthRadius, eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + sWidthRadius, -vertex.y() + sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - sWidthRadius, -vertex.y() + sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -eWidthRadius, eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -eWidthRadius, eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - sWidthRadius, -vertex.y() + sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + sWidthRadius, -vertex.y() + sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, eWidthRadius, eHeightRadius, 0).color(red, green, blue, alpha).endVertex();

            builder.vertex(matrix4f, -eWidthRadius, eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - sWidthRadius, -vertex.y() + sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - sWidthRadius, -vertex.y() - sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -eWidthRadius, -eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -eWidthRadius, -eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - sWidthRadius, -vertex.y() - sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - sWidthRadius, -vertex.y() + sHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -eWidthRadius, eHeightRadius, 0).color(red, green, blue, alpha).endVertex();
        }
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
