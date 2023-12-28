package net.richardsprojects.lotrcompanions.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.richardsprojects.lotrcompanions.npcs.HiredBreeGuard;

import java.util.ArrayList;
import java.util.function.Supplier;

public class UpdateHiredEntityEquipmentPacket {

    private final int entityId;

    private final ArrayList<ItemStack> gear;

    public UpdateHiredEntityEquipmentPacket(int entityId, ArrayList<ItemStack> gear) {
        this.entityId = entityId;
        this.gear = gear;
    }

    public static UpdateHiredEntityEquipmentPacket decode(PacketBuffer buf) {
        System.out.println("Reconstructing UpdateHiredEntityEquipmentPacket packet from server: ");
        ArrayList<ItemStack> gear = new ArrayList<>(6);
        int entityId = buf.readInt();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = buf.readItem();
            gear.add(stack);
        }

        return new UpdateHiredEntityEquipmentPacket(entityId, gear);
    }

    public int getEntityId() {
        return entityId;
    }

    public ArrayList<ItemStack> getGear() {
        return gear;
    }

    public static void encode(UpdateHiredEntityEquipmentPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeItem(msg.gear.get(0));
        buf.writeItem(msg.gear.get(1));
        buf.writeItem(msg.gear.get(2));
        buf.writeItem(msg.gear.get(3));
        buf.writeItem(msg.gear.get(4));
        buf.writeItem(msg.gear.get(5));
    }

    public static void handle(UpdateHiredEntityEquipmentPacket msg, Supplier<NetworkEvent.Context> context) {
        System.out.println("Handling UpdateHiredEntityEquipmentPacket packet from server: ");

        context.get().enqueueWork(() -> {
            if (msg != null) {
            	Minecraft minecraft = Minecraft.getInstance();
                PlayerEntity player = minecraft.player;
                if (player != null) {
                    System.out.println("Entity Id: " + msg.getEntityId());
                    System.out.println("Updated Gear: " + msg.getGear());

                    Entity entity = minecraft.level.getEntity(msg.getEntityId());
                    if (entity != null) {
                        System.out.println("Loaded entity " + entity);
                        if (entity instanceof HiredBreeGuard) {
                            System.out.println("Entity is HiredBreeGuard");
                            HiredBreeGuard breeGuard = (HiredBreeGuard) entity;
                            System.out.println("Preupdate Chest: " + breeGuard.getItemBySlot(EquipmentSlotType.CHEST));
                            breeGuard.setItemSlot(EquipmentSlotType.HEAD, msg.getGear().get(0));
                            breeGuard.setItemSlot(EquipmentSlotType.CHEST, msg.getGear().get(1));
                            breeGuard.setItemSlot(EquipmentSlotType.LEGS, msg.getGear().get(2));
                            breeGuard.setItemSlot(EquipmentSlotType.FEET, msg.getGear().get(3));
                            breeGuard.setItemSlot(EquipmentSlotType.MAINHAND, msg.getGear().get(4));
                            breeGuard.setItemSlot(EquipmentSlotType.OFFHAND, msg.getGear().get(5));

                            breeGuard.inventory.setItem(9, msg.getGear().get(0));
                            breeGuard.inventory.setItem(10, msg.getGear().get(1));
                            breeGuard.inventory.setItem(11, msg.getGear().get(2));
                            breeGuard.inventory.setItem(12, msg.getGear().get(3));
                            breeGuard.inventory.setItem(13, msg.getGear().get(4));
                            breeGuard.inventory.setItem(14, msg.getGear().get(5));
                            System.out.println("Postupdate Chest: " + breeGuard.getItemBySlot(EquipmentSlotType.CHEST));
                        }
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
