package net.richardsprojects.lotrcompanions.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lotr.common.entity.npc.GondorManEntity;
import lotr.common.entity.npc.NPCEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CompanionRenderUtils {

    public static void renderHealthBar(EntityRendererManager mgr, FontRenderer fontrenderer, NPCEntity entity, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int packedLight) {
        double d0 = mgr.distanceToSqr(entity);
        if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(entity, d0)) {

            double healthPercent = entity.getHealth() / entity.getMaxHealth();
            int length = (int) (healthPercent * 10);
            String healthBar = "";
            for (int i = 0; i < length; i++) {
                //char asciiChar = 219;
                healthBar += "\u2588";
            }
            int difference = 10 - length;
            for (int i = 0; i < difference * 2; i++) {
                healthBar += " ";
            }

            ITextComponent text;
            if (healthPercent >= .8) {
                text = new StringTextComponent(healthBar).withStyle(TextFormatting.GREEN).withStyle(TextFormatting.BOLD);
            } else if (healthPercent >= .4) {
                text = new StringTextComponent(healthBar).withStyle(TextFormatting.YELLOW).withStyle(TextFormatting.BOLD);
            } else {
                text = new StringTextComponent(healthBar).withStyle(TextFormatting.RED).withStyle(TextFormatting.BOLD);
            }

            boolean flag = !entity.isDiscrete();
            float f = entity.getBbHeight() + 0.7F;

            p_225629_3_.pushPose();
            p_225629_3_.translate(0.0D, (double) f, 0.0D);
            p_225629_3_.mulPose(mgr.cameraOrientation());
            p_225629_3_.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = p_225629_3_.last().pose();
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int) (f1 * 255.0F) << 24;
            float f2 = (float) (-fontrenderer.width(text) / 2);
            fontrenderer.drawInBatch(text, f2, 0f, 553648127, false, matrix4f, p_225629_4_, flag, j, packedLight);

            if (flag) {
                fontrenderer.drawInBatch(text, f2, (float) 0f, -1, false, matrix4f, p_225629_4_, false, 0, packedLight);
            }

            p_225629_3_.popPose();
        }
    }
}
