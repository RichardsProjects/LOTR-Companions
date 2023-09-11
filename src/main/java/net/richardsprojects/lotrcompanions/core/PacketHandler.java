/**
 * This file has been modified from the Human Companions Mod
 * which can be found here:
 *
 * https://github.com/justinwon777/LOTRCompanions/tree/main
 */
package net.richardsprojects.lotrcompanions.core;


import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.client.screen.CompanionScreen;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;
import net.richardsprojects.lotrcompanions.networking.*;

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
        INSTANCE.registerMessage(id++, SetPatrollingPacket.class, SetPatrollingPacket::encode, SetPatrollingPacket::decode,
                SetPatrollingPacket::handle);
        INSTANCE.registerMessage(id++, ClearTargetPacket.class, ClearTargetPacket::encode, ClearTargetPacket::decode,
                ClearTargetPacket::handle);
        /*INSTANCE.registerMessage(id++, SetStationeryPacket.class, SetStationeryPacket::encode, SetStationeryPacket::decode,
                SetStationeryPacket::handle);*/
        INSTANCE.registerMessage(id++, ReleasePacket.class, ReleasePacket::encode, ReleasePacket::decode,
                ReleasePacket::handle);
    }

    @SuppressWarnings("resource")
    @OnlyIn(Dist.CLIENT)
    public static void openInventory(OpenInventoryPacket packet) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            Entity entity = player.level.getEntity(packet.getEntityId());
            if (entity instanceof HiredGondorSoldier) {
                HiredGondorSoldier companion = (HiredGondorSoldier) entity;
                ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;

                Inventory tmpInventory = new Inventory(15);
                for (int i = 0; i < 9; i++) {
                    tmpInventory.setItem(i, companion.inventory.getItem(i));
                }
                tmpInventory.setItem(9, companion.getItemBySlot(EquipmentSlotType.HEAD));
                tmpInventory.setItem(10, companion.getItemBySlot(EquipmentSlotType.CHEST));
                tmpInventory.setItem(11, companion.getItemBySlot(EquipmentSlotType.LEGS));
                tmpInventory.setItem(12, companion.getItemBySlot(EquipmentSlotType.FEET));
                tmpInventory.setItem(13, companion.getItemBySlot(EquipmentSlotType.MAINHAND));
                tmpInventory.setItem(14, companion.getItemBySlot(EquipmentSlotType.OFFHAND));

                CompanionContainer container = new CompanionContainer(packet.getId(), player.inventory, tmpInventory);
                clientplayerentity.containerMenu = container;
                Minecraft.getInstance().setScreen(new CompanionScreen(container, player.inventory, companion));
            }
        }
    }
}