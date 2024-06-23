package net.richardsprojects.lotrcompanions.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.client.ColorHelper;

public class CompanionHpBar {

    public static void draw(LivingEntity entity, float bgr, float bgg, float bgb, float bgo, float hpr, float hpg, float hpb, float hpo, float fr, float fg, float fb, float fo, float f2r, float f2g, float f2b, float f2o, float textScale, int textposx, int textposy, int textr, int textg, int textb, MatrixStack matrixStackIn, FontRenderer font) {

        // experimental values - tweak these to get it right
        int posx = 0;
        int posy = 0;
        float scale = 1f;

        float scaleX = 0;
        int texto = 255;

        float vit = entity.getMaxHealth(), hp = entity.getHealth();
        float hpWidth = ((float) -font.width((int) hp + "/" + (int) vit) / 2);
        float widthMultiplier = 111.0F / vit;
        int currentWidth = (int)Math.floor((hp * widthMultiplier));
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();

        matrixStackIn.pushPose();
        matrixStackIn.translate(posx, posy, 0.0D);
        matrixStackIn.scale(scale, scale, 1.0F);
        ResourceLocation bar = new ResourceLocation(LOTRCompanions.MOD_ID, "textures/gui/healthbar.png");
        BarRendererHelper.bind(bar);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float scalehp = 1.0F;
        int posxhp = 63;
        int posyhp = 28;
        int startXhp = 0;
        int startYhp = 19;
        int sizeXhp = 150;
        int sizeYhp = 13;

        RenderSystem.color4f(bgr, bgg, bgb, bgo);
        matrixStackIn.scale(scalehp, scalehp, 1.0F);
        if (bgo != 0.0F)
            BarRendererHelper.blit(matrixStackIn, posxhp, posyhp, startXhp, startYhp, sizeXhp, sizeYhp);
        matrixStackIn.translate(0.0D, 0.0D, -0.01D);
        matrixStackIn.pushPose();

        RenderSystem.color4f(f2r, f2g, f2b, f2o);
        matrixStackIn.scale(scalehp + scaleX, scalehp, 1.0F);
        if (f2o != 0.0F)
            BarRendererHelper.blit(matrixStackIn, posxhp, posyhp, startXhp, startYhp, sizeXhp, sizeYhp);
        matrixStackIn.popPose();
        int trueOpacity = (texto < 25) ? 25 : texto;
        matrixStackIn.translate(textposx, textposy, -0.03D);
        matrixStackIn.scale(textScale, textScale, 1.0F);
        int k = ColorHelper.ARGB32.color(trueOpacity, textr, textg, textb);
        BarRendererHelper.drawString(matrixStackIn, font, (int) hp + "/" + (int) vit, (int)hpWidth + 137, 31, k, false);
        RenderSystem.disableBlend();
        matrixStackIn.popPose();
        RenderSystem.disableDepthTest();
    }

}
