package net.richardsprojects.lotrcompanions.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;

public class CompanionScreen extends ContainerScreen<CompanionContainer> implements IHasContainer<CompanionContainer> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(LOTRCompanions.MOD_ID,"textures/inventory.png");

    private final HiredGondorSoldier companion;
    DecimalFormat df = new DecimalFormat("#.#");
    int sidebarX;
    int row1;
    int col1;

    public CompanionScreen(CompanionContainer p_98409_, PlayerInventory p_98410_, HiredGondorSoldier companion) {
        super(p_98409_, p_98410_, companion.getName());

        this.companion = companion;
        this.passEvents = false;
        this.imageHeight = 256;
        this.inventoryLabelY = 130;
        this.imageWidth = 226;
        df.setRoundingMode(RoundingMode.CEILING);
        sidebarX = 110;
    }

    @Override
    public void render(MatrixStack p_98418_, int p_98419_, int p_98420_, float p_98421_) {
        this.renderBackground(p_98418_);
        super.render(p_98418_, p_98419_, p_98420_, p_98421_);
        this.renderTooltip(p_98418_, p_98419_, p_98420_);
    }

    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        row1 = topPos + 66;
        col1 = leftPos + sidebarX + 3;
    }

    protected void renderBg(MatrixStack p_98413_, float p_98414_, int p_98415_, int p_98416_) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(CONTAINER_BACKGROUND);
        int i = (this.width - this.imageWidth) / 2;
        int j = ((this.height - this.imageHeight) / 2) + 2;
        this.blit(p_98413_, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected void renderLabels(MatrixStack matrix, int p_230451_2_, int p_230451_3_) {
        this.font.draw(matrix, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        this.font.draw(matrix, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);

        int classHeight = this.titleLabelY + 14;
        int classLeft = sidebarX;
        StringTextComponent classTitle = new StringTextComponent("Companion:");
        StringTextComponent healthTitle = new StringTextComponent("Health");
        StringTextComponent health =
                new StringTextComponent(df.format(companion.getHealth()) + "/" + (int) companion.getMaxHealth());

        this.font.draw(matrix, classTitle.withStyle(TextFormatting.UNDERLINE), sidebarX, this.titleLabelY + 6,
                4210752);

        if (companion instanceof HiredGondorSoldier) {
            this.font.draw(matrix, "Gondor Soldier", classLeft, classHeight, 4210752);
        }

        this.font.draw(matrix, healthTitle.withStyle(TextFormatting.UNDERLINE), sidebarX, this.titleLabelY + 26,
                4210752);
        this.font.draw(matrix, health, sidebarX, this.titleLabelY + 37, 4210752);

        this.font.draw(matrix, "Level " + companion.getExpLvl(), sidebarX, this.titleLabelY + 49,
                4210752);
        this.font.draw(matrix, "Xp " + companion.getCurrentXp() + "/" + companion.getMaxXp(), sidebarX, this.titleLabelY + 58,
                4210752);
        this.font.draw(matrix, "Mob Kills: " + companion.getMobKills(), sidebarX, this.titleLabelY + 67,
                4210752);
    }

}
