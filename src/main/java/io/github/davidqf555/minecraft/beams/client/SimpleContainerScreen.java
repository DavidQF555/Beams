package io.github.davidqf555.minecraft.beams.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class SimpleContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private final ResourceLocation texture;

    public SimpleContainerScreen(ResourceLocation texture, T container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.texture = texture;
        imageHeight = 133;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partial) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partial);
        renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack stack, float partial, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, texture);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        blit(stack, x, y, 0, 0, imageWidth, imageHeight);
    }
}
