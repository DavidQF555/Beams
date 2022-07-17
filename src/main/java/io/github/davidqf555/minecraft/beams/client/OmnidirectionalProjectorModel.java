package io.github.davidqf555.minecraft.beams.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class OmnidirectionalProjectorModel extends Model {

    private final ModelPart model;

    public OmnidirectionalProjectorModel(ModelPart model) {
        super(RenderType::entityCutoutNoCull);
        this.model = model.getChild("projector");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("projector", CubeListBuilder.create().texOffs(0, 0).addBox(-8, -8, -8, 16, 16, 16, new CubeDeformation(-4, -4, -4)), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack p_225598_1_, VertexConsumer p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        model.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }

}
