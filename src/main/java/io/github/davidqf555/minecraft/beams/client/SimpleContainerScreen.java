package io.github.davidqf555.minecraft.beams.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SimpleContainerScreen<T extends Container> extends ContainerScreen<T> {

    private final ResourceLocation texture;

    public SimpleContainerScreen(ResourceLocation texture, T container, PlayerInventory inventory, ITextComponent name) {
        super(container, inventory, name);
        this.texture = texture;
        imageHeight = 133;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partial) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partial);
        renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack stack, float partial, int mouseX, int mouseY) {
        minecraft.getTextureManager().bind(texture);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        blit(stack, x, y, 0, 0, imageWidth, imageHeight);
    }
}
