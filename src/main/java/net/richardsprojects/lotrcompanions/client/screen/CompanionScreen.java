package net.richardsprojects.lotrcompanions.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;
import net.richardsprojects.lotrcompanions.core.PacketHandler;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;
import net.richardsprojects.lotrcompanions.networking.SetAlertPacket;
import net.richardsprojects.lotrcompanions.networking.SetPatrollingPacket;
import net.richardsprojects.lotrcompanions.networking.SetStationaryPacket;

public class CompanionScreen extends ContainerScreen<CompanionContainer> implements IHasContainer<CompanionContainer> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(LOTRCompanions.MOD_ID,"textures/inventory.png");

    private static final ResourceLocation ALERT_BUTTON = new ResourceLocation(LOTRCompanions.MOD_ID, "textures/alertbutton.png");
    private static final ResourceLocation PATROL_BUTTON = new ResourceLocation(LOTRCompanions.MOD_ID, "textures" +
            "/patrolbutton.png");
    private static final ResourceLocation CLEAR_BUTTON = new ResourceLocation(LOTRCompanions.MOD_ID, "textures" +
            "/clearbutton.png");
    private static final ResourceLocation STATIONERY_BUTTON = new ResourceLocation(LOTRCompanions.MOD_ID, "textures" +
            "/stationerybutton.png");
    private static final ResourceLocation RELEASE_BUTTON = new ResourceLocation(LOTRCompanions.MOD_ID, "textures" +
            "/releasebutton.png");

    private final HiredGondorSoldier companion;
    DecimalFormat df = new DecimalFormat("#.#");
    int sidebarX;
    int row1;
    int col1;

    // buttons
    private CompanionButton alertButton;
    private CompanionButton patrolButton;
    private CompanionButton clearButton;
    private CompanionButton stationeryButton;

    public CompanionScreen(CompanionContainer p_98409_, PlayerInventory p_98410_, HiredGondorSoldier companion) {
        super(p_98409_, p_98410_, companion.getName());

        this.companion = companion;
        this.passEvents = false;
        this.imageHeight = 256;
        this.inventoryLabelY = 130;
        this.imageWidth = 226;
        df.setRoundingMode(RoundingMode.CEILING);
        sidebarX = 90;
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

        this.alertButton = addButton(new CompanionButton("alert", sidebarX, this.titleLabelY + 83, 16, 12, 0, 0, 13,
                ALERT_BUTTON,
                btn -> {
                    PacketHandler.INSTANCE.sendToServer(new SetAlertPacket(companion.getId()));
                })
        );
        this.patrolButton = addButton(new CompanionButton("patrolling", sidebarX, this.titleLabelY + 93, 16, 12,
                0, 0
                ,13,
                PATROL_BUTTON,
                btn -> {
                    PacketHandler.INSTANCE.sendToServer(new SetPatrollingPacket(companion.getId()));
                })
        );
        this.stationeryButton = addButton(new CompanionButton("stationery", sidebarX, this.titleLabelY + 93,
                16, 12, 0, 0, 13,
                STATIONERY_BUTTON,
                btn -> {
                    PacketHandler.INSTANCE.sendToServer(new SetStationaryPacket(companion.getId()));
                })
        );
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

        this.font.draw(matrix, classTitle.withStyle(TextFormatting.UNDERLINE), sidebarX, this.titleLabelY + 12,
                4210752);

        if (companion instanceof HiredGondorSoldier) {
            this.font.draw(matrix, "Gondor Soldier", sidebarX, this.titleLabelY + 22, 4210752);
        }

        this.font.draw(matrix, healthTitle.withStyle(TextFormatting.UNDERLINE), sidebarX, this.titleLabelY + 32,
                4210752);
        this.font.draw(matrix, health, sidebarX, this.titleLabelY + 42, 4210752);

        this.font.draw(matrix, "Level " + companion.getExpLvl(), sidebarX, this.titleLabelY + 56,
                4210752);
        this.font.draw(matrix, "Xp " + companion.getCurrentXp() + "/" + companion.getMaxXp(), sidebarX, this.titleLabelY + 64,
                4210752);
        this.font.draw(matrix, "Mob Kills: " + companion.getMobKills(), sidebarX, this.titleLabelY + 73,
                4210752);
    }

    class CompanionButton extends ImageButton {

        private String name;
        private int xTexStart;

        public CompanionButton(String name, int p_94269_, int p_94270_, int p_94271_, int p_94272_, int p_94273_,
                               int p_94274_,
                               int p_94275_, ResourceLocation p_94276_,
                               Button.IPressable p_94277_) {
            super(p_94269_, p_94270_, p_94271_, p_94272_, p_94273_, p_94274_, p_94275_, p_94276_, p_94277_);
            this.name = name;
        }

        @Override
        public void renderButton(MatrixStack p_94282_, int p_94283_, int p_94284_, float p_94285_) {
            if (this.name.equals("alert")) {
                if (CompanionScreen.this.companion.isAlert()) {
                    this.xTexStart = 0;
                } else {
                    this.xTexStart = 17;
                }
            } else if (this.name.equals("patrolling")) {
                if (CompanionScreen.this.companion.isFollowing()) {
                    this.xTexStart = 0;
                } else if (CompanionScreen.this.companion.isPatrolling()){
                    this.xTexStart = 17;
                } else {
                    this.xTexStart = 34;
                }
            } else if (this.name.equals("stationery")) {
                if (CompanionScreen.this.companion.isStationary()) {
                    this.xTexStart = 0;
                } else {
                    this.xTexStart = 17;
                }
            }
            RenderSystem.enableBlend();
            super.renderButton(p_94282_, p_94283_, p_94284_, p_94285_);
            RenderSystem.disableBlend();
        }
    }
}
