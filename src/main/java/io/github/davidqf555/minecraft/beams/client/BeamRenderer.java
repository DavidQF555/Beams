package io.github.davidqf555.minecraft.beams.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class BeamRenderer<T extends BeamEntity> extends EntityRenderer<T> {

    public BeamRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        Vector3d dif = entityIn.getEnd().subtract(entityIn.position());
        float length = MathHelper.sqrt(dif.x() * dif.x() + dif.y() * dif.y() + dif.z() * dif.z());
        Vector3f vertex = new Vector3f(0, 0, length);
        float yaw = (float) (Math.PI / 2 - MathHelper.atan2(-dif.z(), dif.x()));//pi/2
        float pitch = (float) Math.asin(MathHelper.clamp(dif.y() / length, -1, 1));//
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YN.rotation(yaw));
        matrixStackIn.mulPose(Vector3f.XP.rotation(pitch));
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = matrixStackIn.last().pose();
        int color = entityIn.getColor();
        float alpha = ColorHelper.PackedColor.alpha(color) / 255f;
        float red = ColorHelper.PackedColor.red(color) / 255f;
        float green = ColorHelper.PackedColor.green(color) / 255f;
        float blue = ColorHelper.PackedColor.blue(color) / 255f;
        int layers = entityIn.getLayers();
        double startWidthRadius = entityIn.getStartWidth() / 2;
        double startHeightRadius = entityIn.getStartHeight() / 2;
        double endWidthRadius = entityIn.getEndWidth() / 2;
        double endHeightRadius = entityIn.getEndHeight() / 2;
        for (int i = 1; i <= layers; i++) {
            float sWidthRadius = (float) (startWidthRadius * i / layers);
            float sHeightRadius = (float) (startHeightRadius * i / layers);
            float eWidthRadius = (float) (endWidthRadius * i / layers);
            float eHeightRadius = (float) (endHeightRadius * i / layers);

            builder.vertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - eWidthRadius, -vertex.y() - eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + eWidthRadius, -vertex.y() - eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, sWidthRadius, -sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, sWidthRadius, -sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + eWidthRadius, -vertex.y() - eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - eWidthRadius, -vertex.y() - eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).color(red, green, blue, alpha).endVertex();

            builder.vertex(matrix4f, sWidthRadius, -sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + eWidthRadius, -vertex.y() - eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + eWidthRadius, -vertex.y() + eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, sWidthRadius, sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, sWidthRadius, sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + eWidthRadius, -vertex.y() + eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + eWidthRadius, -vertex.y() - eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, sWidthRadius, -sHeightRadius, 0).color(red, green, blue, alpha).endVertex();

            builder.vertex(matrix4f, sWidthRadius, sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + eWidthRadius, -vertex.y() + eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - eWidthRadius, -vertex.y() + eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -sWidthRadius, sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -sWidthRadius, sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - eWidthRadius, -vertex.y() + eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() + eWidthRadius, -vertex.y() + eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, sWidthRadius, sHeightRadius, 0).color(red, green, blue, alpha).endVertex();

            builder.vertex(matrix4f, -sWidthRadius, sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - eWidthRadius, -vertex.y() + eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - eWidthRadius, -vertex.y() - eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - eWidthRadius, -vertex.y() - eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -vertex.x() - eWidthRadius, -vertex.y() + eHeightRadius, -vertex.z()).color(red, green, blue, alpha).endVertex();
            builder.vertex(matrix4f, -sWidthRadius, sHeightRadius, 0).color(red, green, blue, alpha).endVertex();
        }
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
