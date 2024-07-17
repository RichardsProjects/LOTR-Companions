package net.richardsprojects.lotrcompanions.client.eventhandlers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lotr.common.entity.npc.ExtendedHirableEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.client.render.CompanionHpBar;

import java.util.UUID;

import static net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance;

public class RenderHealthbars {

    @SubscribeEvent
    public static void renderHpBar(RenderNameplateEvent event) {
        if (event.getEntity() instanceof ExtendedHirableEntity) {
            ExtendedHirableEntity hired = (ExtendedHirableEntity) event.getEntity();
            if (hired.getOwnerUUID() == null ||
                    LOTRCompanions.usersUUID.equals(null) ||
                    !hired.getOwnerUUID().equals(LOTRCompanions.usersUUID)) {
                return;
            }

            Minecraft minecraft = Minecraft.getInstance();
            MatrixStack matrixStackIn = event.getMatrixStack();

            double d0 = minecraft.getEntityRenderDispatcher().distanceToSqr(event.getEntity());
            FontRenderer fontrenderer = event.getEntityRenderer().getFont();
            IRenderTypeBuffer.Impl buffer = minecraft.renderBuffers().bufferSource();
            Matrix4f matrix4f = matrixStackIn.last().pose();

            if (isNameplateInRenderDistance(event.getEntity(), d0)) {
                float f = event.getEntity().getBbHeight() + 1.2F;
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.0D, f, 0.0D);
                matrixStackIn.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
                matrixStackIn.scale(-0.025F, -0.025F, 0.025F);

                CompanionHpBar.draw(
                        (LivingEntity) event.getEntity(),
                        100 / 100.0F, // text scale default 1X
                        matrixStackIn,
                        fontrenderer
                );

                matrixStackIn.popPose();
                RenderSystem.disableDepthTest();
            }
        }
    }

}
