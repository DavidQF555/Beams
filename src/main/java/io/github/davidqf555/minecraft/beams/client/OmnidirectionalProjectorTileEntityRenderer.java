package io.github.davidqf555.minecraft.beams.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.DirectionalProjectorTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class OmnidirectionalProjectorTileEntityRenderer implements BlockEntityRenderer<DirectionalProjectorTileEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Beams.ID, "textures/block/omnidirectional_projector.png");
    private final OmnidirectionalProjectorModel model;

    public OmnidirectionalProjectorTileEntityRenderer(BlockEntityRendererProvider.Context dispatcher) {
        model = new OmnidirectionalProjectorModel(dispatcher.bakeLayer(ClientRegistry.OMNIDIRECTIONAL_PROJECTOR));
    }

    @Override
    public void render(DirectionalProjectorTileEntity p_225616_1_, float p_225616_2_, PoseStack p_225616_3_, MultiBufferSource p_225616_4_, int p_225616_5_, int p_225616_6_) {
        Vec3 dir = p_225616_1_.getDirection();
        double yRot = Mth.atan2(dir.x(), dir.z());
        double xRot = Mth.atan2(dir.y(), Mth.sqrt((float) (dir.x() * dir.x() + dir.z() * dir.z()))) + Math.PI;
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
