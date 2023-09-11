/**
 * This file has been modified from the Human Companions Mod
 * which can be found here:
 *
 * https://github.com/justinwon777/HumanCompanions/tree/main
 */

package net.richardsprojects.lotrcompanions.entity;

import lotr.common.entity.npc.GondorSoldierEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.*;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;
import net.richardsprojects.lotrcompanions.core.PacketHandler;
import net.richardsprojects.lotrcompanions.entity.ai.*;
import net.richardsprojects.lotrcompanions.networking.OpenInventoryPacket;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class HiredGondorSoldier extends GondorSoldierEntity {

    public HiredGondorSoldier(EntityType entityType, World level) {
        super(entityType, level);
        this.setTame(false);
    }

    protected static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(HiredGondorSoldier.class, DataSerializers.BYTE);
    protected static final DataParameter<Optional<UUID>> DATA_OWNERUUID_ID = EntityDataManager.defineId(HiredGondorSoldier.class, DataSerializers.OPTIONAL_UUID);
    private static final DataParameter<Integer> LVL = EntityDataManager.defineId(HiredGondorSoldier.class,
            DataSerializers.INT);
    private static final DataParameter<Integer> CURRENT_XP = EntityDataManager.defineId(HiredGondorSoldier.class,
            DataSerializers.INT);
    private static final DataParameter<Integer> MAX_XP = EntityDataManager.defineId(HiredGondorSoldier.class,
            DataSerializers.INT);

    private static final DataParameter<Integer> KILLS = EntityDataManager.defineId(HiredGondorSoldier.class, DataSerializers.INT);

    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.defineId(HiredGondorSoldier.class,
            DataSerializers.BOOLEAN);

    public Inventory inventory = new Inventory(9);

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(LVL, 1);
        this.entityData.define(CURRENT_XP, 0);
        this.entityData.define(MAX_XP, 3);
        this.entityData.define(KILLS, 0);
        this.entityData.define(FOLLOWING, false);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return GondorSoldierEntity.regAttrs()
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D);
    }

    public void setExpLvl(int lvl) {
        this.entityData.set(LVL, lvl);
    }

    public int getExpLvl() {
        return this.entityData.get(LVL);
    }

    public void setMobKills(int kills) {
        this.entityData.set(KILLS, kills);
    }

    public int getMobKills() {
        return this.entityData.get(KILLS);
    }

    public void setCurrentXp(int currentXp) {
        this.entityData.set(CURRENT_XP, currentXp);
    }

    public int getCurrentXp() {
        return this.entityData.get(CURRENT_XP);
    }

    public void setMaxXp(int maxXp) {
        this.entityData.set(MAX_XP, maxXp);
    }

    public int getMaxXp() {
        return this.entityData.get(MAX_XP);
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        System.out.println("Mob Interact Called!");

        ItemStack itemstack = player.getItemInHand(hand);
        if (hand == Hand.MAIN_HAND) {
            if (!this.isTame() && !this.level.isClientSide()) {
                System.out.println("Is not tamed");

                // TODO: temporarily allow taming with cooked chicken
                if (itemstack.getItem().equals(Items.COOKED_CHICKEN)) {
                    this.tame(player);
                    player.sendMessage(new TranslationTextComponent("chat.type.text", this.getDisplayName(),
                            new StringTextComponent("Thanks!")), this.getUUID());
                    player.sendMessage(new StringTextComponent("Companion added"), this.getUUID());
                    //setPatrolPos(null);
                    //setPatrolling(false);
                    //setFollowing(true);
                    //setPatrolRadius(4);
                    //patrolGoal.radius = 4;
                    //moveBackGoal.radius = 4;
                }
            } else {
                System.out.println("Is tamed");

                if (this.isAlliedTo(player)) {
                    System.out.println("Is allied to player");
                    if(player.isShiftKeyDown()) {
                        if (!this.level.isClientSide()) {
                            // TODO: reimplement stay
                        }
                    } else {
                        if (!this.level.isClientSide()) {
                            System.out.println("Attempt to open GUI");
                            this.openGui((ServerPlayerEntity) player);
                        }
                    }
                }
                return ActionResultType.sidedSuccess(this.level.isClientSide());
            }
            return ActionResultType.sidedSuccess(this.level.isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    public boolean isAlliedTo(Entity p_184191_1_) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (p_184191_1_ == livingentity) {
                return true;
            }

            if (livingentity != null) {
                return livingentity.isAlliedTo(p_184191_1_);
            }
        }

        return super.isAlliedTo(p_184191_1_);
    }

    public void openGui(ServerPlayerEntity player) {
        if (player.containerMenu != player.inventoryMenu) {
            player.closeContainer();
        }

        player.nextContainerCounter();
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OpenInventoryPacket(
                player.containerCounter, this.inventory.getContainerSize(), this.getId()));

        Inventory tmpInventory = new Inventory(15);
        for (int i = 0; i < 9; i++) {
            tmpInventory.setItem(i, inventory.getItem(i));
        }
        tmpInventory.setItem(9, getItemBySlot(EquipmentSlotType.HEAD));
        tmpInventory.setItem(10, getItemBySlot(EquipmentSlotType.CHEST));
        tmpInventory.setItem(11, getItemBySlot(EquipmentSlotType.LEGS));
        tmpInventory.setItem(12, getItemBySlot(EquipmentSlotType.FEET));
        tmpInventory.setItem(13, getItemBySlot(EquipmentSlotType.MAINHAND));
        tmpInventory.setItem(14, getItemBySlot(EquipmentSlotType.OFFHAND));

        player.containerMenu = new CompanionContainer(
                player.containerCounter, player.inventory, tmpInventory
        );

        player.containerMenu.addSlotListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.containerMenu));
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse((UUID)null);
    }

    public void setOwnerUUID(@Nullable UUID p_184754_1_) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(p_184754_1_));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        //this.goalSelector.addGoal(0, new EatGoal(this));
        //this.goalSelector.addGoal(1, new CustomSitGoal(this));
        //this.goalSelector.addGoal(2, new AvoidCreeperGoal(this, CreeperEntity.class, 10.0F, 1.5D, 1.5D));
        //this.goalSelector.addGoal(3, new MoveBackToGuardGoal(this));
        this.goalSelector.addGoal(3, new CustomFollowOwnerGoal(this, 1.3D, 8.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new CustomWaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(9, new LowHealthGoal(this));
        this.targetSelector.addGoal(1, new CustomOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CustomOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    public boolean wantsToAttack(LivingEntity p_142018_1_, LivingEntity p_142018_2_) {
        return true;
    }

    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        if (this.getOwnerUUID() != null) {
            tag.putUUID("Owner", this.getOwnerUUID());
        }

        tag.put("inventory", this.inventory.createTag());
        /*tag.putBoolean("Alert", this.isAlert());
        tag.putBoolean("Hunting", this.isHunting());
        tag.putBoolean("Patrolling", this.isPatrolling());*/
        tag.putBoolean("following", this.isFollowing());
        /*tag.putBoolean("Guarding", this.isGuarding());
        tag.putBoolean("Stationery", this.isStationery());
        tag.putInt("radius", this.getPatrolRadius());
        tag.putInt("baseHealth", this.getBaseHealth());*/
        /*tag.putFloat("XpP", this.experienceProgress);
        tag.putInt("XpLevel", this.experienceLevel);
        tag.putInt("XpTotal", this.totalExperience);*/

        /*if (this.getPatrolPos() != null) {
            int[] patrolPos = {this.getPatrolPos().getX(), this.getPatrolPos().getY(), this.getPatrolPos().getZ()};
            tag.putIntArray("patrol_pos", patrolPos);
        }*/
    }

    public boolean isFollowing() {
        return this.entityData.get(FOLLOWING);
    }

    public void setFollowing(boolean following) {
        this.entityData.set(FOLLOWING, following);
    }

    public void tame(PlayerEntity p_193101_1_) {
        this.setTame(true);
        this.setOwnerUUID(p_193101_1_.getUUID());
        System.out.println("Tame called");
    }

    public void setTame(boolean p_70903_1_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_70903_1_) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 4));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -5));
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    /*
        public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        UUID lvt_2_2_;
        if (p_70037_1_.hasUUID("Owner")) {
            lvt_2_2_ = p_70037_1_.getUUID("Owner");
        } else {
            String lvt_3_1_ = p_70037_1_.getString("Owner");
            lvt_2_2_ = PreYggdrasilConverter.convertMobOwnerIfNecessary(this.getServer(), lvt_3_1_);
        }

        if (lvt_2_2_ != null) {
            try {
                this.setOwnerUUID(lvt_2_2_);
                this.setTame(true);
            } catch (Throwable var4) {
                this.setTame(false);
            }
        }

        this.orderedToSit = p_70037_1_.getBoolean("Sitting");
        this.setInSittingPose(this.orderedToSit);
    }
     */

    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);

        UUID uuid;
        if (tag.hasUUID("Owner")) {
            uuid = tag.getUUID("Owner");
        } else {
            String ownerString = tag.getString("Owner");
            uuid = PreYggdrasilConverter.convertMobOwnerIfNecessary(this.getServer(), ownerString);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
                this.setTame(true);
            } catch (Throwable var4) {
                this.setTame(false);
            }
        }

        if (tag.contains("inventory", 9)) {
            this.inventory.fromTag(tag.getList("inventory", 10));
        }
    }

    public void tick() {
        /*if (!this.level.isClientSide()) {
            checkSword();
        }*/
        super.tick();
    }

    public void die(DamageSource p_70645_1_) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayerEntity) {
            // TODO: Re-implement this
            //this.getOwner().sendMessage(TextComponentUtils., Util.NIL_UUID);
        }

        super.die(p_70645_1_);
    }

    public boolean isTame() {
        return (this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
    }
}
