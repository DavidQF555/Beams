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
    public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, int p_350308_) {
        model.render(p_103111_, p_103112_, p_103113_, p_103114_, p_350308_);
    }

}
