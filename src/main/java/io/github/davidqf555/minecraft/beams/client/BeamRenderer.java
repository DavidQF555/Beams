package io.github.davidqf555.minecraft.beams.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BeamRenderer<T extends BeamEntity> extends EntityRenderer<T> {

    public BeamRenderer(EntityRendererProvider.Context manager) {
        super(manager);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        Vec3 dif = entityIn.getEnd().subtract(entityIn.position());
        double length = dif.length();
        float yaw = (float) (Math.PI / 2 - Mth.atan2(-dif.z(), dif.x()));
        float pitch = (float) Math.asin(Mth.clamp(dif.y() / length, -1, 1));
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YN.rotation(yaw));
        matrixStackIn.mulPose(Axis.XP.rotation(pitch));
        VertexConsumer builder = bufferIn.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = matrixStackIn.last().pose();
        int color = entityIn.getColor();
        float alpha = FastColor.ARGB32.alpha(color) / 255f;
        float red = FastColor.ARGB32.red(color) / 255f;
        float green = FastColor.ARGB32.green(color) / 255f;
        float blue = FastColor.ARGB32.blue(color) / 255f;
        int layers = entityIn.getLayers();
        float startWidthRadius = (float) (entityIn.getStartWidth() / 2);
        float startHeightRadius = (float) (entityIn.getStartHeight() / 2);
        float endWidthRadius = (float) (entityIn.getEndWidth() / 2);
        float endHeightRadius = (float) (entityIn.getEndHeight() / 2);
        for (int i = 1; i <= layers; i++) {
            float sWidthRadius = startWidthRadius * i / layers;
            float sHeightRadius = startHeightRadius * i / layers;
            float eWidthRadius = endWidthRadius * i / layers;
            float eHeightRadius = endHeightRadius * i / layers;

            builder.addVertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);

            builder.addVertex(matrix4f, sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);

            builder.addVertex(matrix4f, sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);

            builder.addVertex(matrix4f, -sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
        }
        builder.addVertex(matrix4f, -startWidthRadius, -startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -startWidthRadius, startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, startWidthRadius, startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, startWidthRadius, -startHeightRadius, 0).setColor(red, green, blue, alpha);

        builder.addVertex(matrix4f, startWidthRadius, -startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, startWidthRadius, startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -startWidthRadius, startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -startWidthRadius, -startHeightRadius, 0).setColor(red, green, blue, alpha);

        builder.addVertex(matrix4f, -endWidthRadius, -endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -endWidthRadius, endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, endWidthRadius, endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, endWidthRadius, -endHeightRadius, (float) -length).setColor(red, green, blue, alpha);

        builder.addVertex(matrix4f, endWidthRadius, -endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, endWidthRadius, endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -endWidthRadius, endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -endWidthRadius, -endHeightRadius, (float) -length).setColor(red, green, blue, alpha);

        matrixStackIn.popPose();
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
