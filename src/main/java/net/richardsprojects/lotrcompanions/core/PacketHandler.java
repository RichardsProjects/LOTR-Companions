/**
 * This file has been modified from the Human Companions Mod
 * which can be found here:
 *
 * https://github.com/justinwon777/LOTRCompanions/tree/main
 */
package net.richardsprojects.lotrcompanions.core;

import com.github.justinwon777.LOTRCompanions.client.CompanionScreen;
import com.github.justinwon777.LOTRCompanions.container.CompanionContainer;
import com.github.justinwon777.LOTRCompanions.entity.AbstractHumanCompanionEntity;
import com.github.justinwon777.LOTRCompanions.networking.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.entity.AbstractHumanCompanionEntity;
import net.richardsprojects.lotrcompanions.networking.OpenInventoryPacket;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE =
            NetworkRegistry.newSimpleChannel(new ResourceLocation(LOTRCompanions.MOD_ID,
                    "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, OpenInventoryPacket.class, OpenInventoryPacket::encode, OpenInventoryPacket::decode, OpenInventoryPacket::handle);
        INSTANCE.registerMessage(id++, SetAlertPacket.class, SetAlertPacket::encode, SetAlertPacket::decode,
                SetAlertPacket::handle);
        INSTANCE.registerMessage(id++, SetHuntingPacket.class, SetHuntingPacket::encode, SetHuntingPacket::decode,
                SetHuntingPacket::handle);
        INSTANCE.registerMessage(id++, SetPatrollingPacket.class, SetPatrollingPacket::encode, SetPatrollingPacket::decode,
                SetPatrollingPacket::handle);
        INSTANCE.registerMessage(id++, ClearTargetPacket.class, ClearTargetPacket::encode, ClearTargetPacket::decode,
                ClearTargetPacket::handle);
        INSTANCE.registerMessage(id++, SetStationeryPacket.class, SetStationeryPacket::encode, SetStationeryPacket::decode,
                SetStationeryPacket::handle);
        INSTANCE.registerMessage(id++, ReleasePacket.class, ReleasePacket::encode, ReleasePacket::decode,
                ReleasePacket::handle);
    }

    @SuppressWarnings("resource")
    @OnlyIn(Dist.CLIENT)
    public static void openInventory(OpenInventoryPacket packet) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            Entity entity = player.level.getEntity(packet.getEntityId());
            if (entity instanceof AbstractHumanCompanionEntity) {
                AbstractHumanCompanionEntity companion = (AbstractHumanCompanionEntity) entity;
                ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
                CompanionContainer container = new CompanionContainer(packet.getId(), player.inventory, companion.inventory);
                clientplayerentity.containerMenu = container;
                Minecraft.getInstance().setScreen(new CompanionScreen(container, player.inventory, companion));
            }
        }
    }
}
