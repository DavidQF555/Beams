package io.github.davidqf555.minecraft.beams.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.te.OmnidirectionalProjectorTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class OmnidirectionalProjectorTileEntityRenderer extends TileEntityRenderer<OmnidirectionalProjectorTileEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Beams.ID, "textures/block/omnidirectional_projector.png");
    private final OmnidirectionalProjectorModel model;

    public OmnidirectionalProjectorTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
        model = new OmnidirectionalProjectorModel();
    }

    @Override
    public void render(OmnidirectionalProjectorTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
        Vector3d dir = p_225616_1_.getDirection();
        double yRot = MathHelper.atan2(dir.x(), dir.z());
        double xRot = MathHelper.atan2(dir.y(), MathHelper.sqrt(dir.x() * dir.x() + dir.z() * dir.z())) + Math.PI;
        p_225616_3_.pushPose();
        p_225616_3_.translate(0.5, 0.5, 0.5);
        p_225616_3_.mulPose(Vector3f.YP.rotation((float) yRot));
        p_225616_3_.mulPose(Vector3f.XN.rotation((float) xRot));
        model.renderToBuffer(p_225616_3_, p_225616_4_.getBuffer(model.renderType(getTexture())), p_225616_5_, p_225616_6_, 1, 1, 1, 1);
        p_225616_3_.popPose();
    }

    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

}
