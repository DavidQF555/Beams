package io.github.davidqf555.minecraft.beams.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class OmnidirectionalProjectorModel extends Model {

    private final ModelRenderer model;

    public OmnidirectionalProjectorModel() {
        super(RenderType::entityCutoutNoCull);
        model = new ModelRenderer(0, 0, 32, 16)
                .addBox(-4, -4, -4, 8, 8, 8);
    }

    @Override
    public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        model.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }

}
