package net.richardsprojects.lotrcompanions.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.gui.AbstractGui;

public class BarRendererHelper {

        public static boolean isMouseOverArea(double mouseX, double mouseY, int posX, int posY, int sizeX, int sizeY) {
            return (mouseX >= posX && mouseX < (posX + sizeX) && mouseY >= posY && mouseY < (posY + sizeY));
        }

        public static void bind(ResourceLocation res) {
            Minecraft.getInstance().getTextureManager().bind(res);
        }

        public static void blit(MatrixStack matrixStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
            AbstractGui.blit(matrixStack, x, y, 0, uOffset, vOffset, uWidth, vHeight, 256, 256);
        }

        public static void drawString(MatrixStack stack, FontRenderer font, String text, int x, int y, int color, boolean shadow) {
            drawShadow(stack, font, text, x, y, color, shadow);
        }

        public static int drawShadow(MatrixStack stack, FontRenderer font, String text, float x, float y, int color, boolean shadow) {
            return drawInternal(font, text, x, y, color, stack.last().pose(), false, shadow);
        }

        public static int drawInternal(FontRenderer font, String text, float x, float y, int color, Matrix4f matrix, boolean p_92809_, boolean shadow) {
            if (text == null)
                return 0;
            IRenderTypeBuffer.Impl multibuffersource$buffersource = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
            int i = font.drawInBatch(text, x, y, color, shadow, matrix, (IRenderTypeBuffer)multibuffersource$buffersource, false, 0, 15728880, p_92809_);
            multibuffersource$buffersource.endBatch();
            return i;
        }
}
