package net.richardsprojects.lotrcompanions.entity;

import lotr.common.entity.npc.BreeGuardEntity;
import lotr.common.entity.npc.GondorSoldierEntity;
import lotr.common.init.LOTRItems;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
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
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class HiredBreeGuard extends BreeGuardEntity implements HirableUnit {

    protected static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(HiredBreeGuard.class, DataSerializers.BYTE);
    protected static final DataParameter<Optional<UUID>> DATA_OWNERUUID_ID = EntityDataManager.defineId(HiredBreeGuard.class, DataSerializers.OPTIONAL_UUID);
    private static final DataParameter<Integer> LVL = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.INT);
    private static final DataParameter<Integer> CURRENT_XP = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.INT);

    private static final DataParameter<Integer> BASE_HEALTH = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.INT);
    private static final DataParameter<Integer> MAX_XP = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.INT);

    private static final DataParameter<Integer> KILLS = EntityDataManager.defineId(HiredBreeGuard.class, DataSerializers.INT);

    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.BOOLEAN);

    private static final DataParameter<Boolean> STATIONARY = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> PATROLLING = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ALERT = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GUARDING = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.BOOLEAN);

    private static final DataParameter<Float> TMP_LAST_HEALTH = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.FLOAT);
    private boolean tmpHealthLoaded = false;
    private boolean healthUpdateFromTmpHealth = false;

    private static final DataParameter<Boolean> INVENTORY_OPEN = EntityDataManager.defineId(HiredBreeGuard.class,
            DataSerializers.BOOLEAN);

    // 9 inventory slots + 6 equipment slots
    public Inventory inventory = new Inventory(15);

    public HiredBreeGuard(EntityType<? extends BreeGuardEntity> type, World w) {
        super(type, w);

        inventory.setItem(9, new ItemStack(Items.IRON_HELMET));
        inventory.setItem(10, new ItemStack(Items.LEATHER_CHESTPLATE));
        inventory.setItem(11, new ItemStack(Items.CHAINMAIL_LEGGINGS));
        inventory.setItem(12, new ItemStack(Items.CHAINMAIL_BOOTS));
        inventory.setItem(13, new ItemStack(LOTRItems.IRON_SPEAR.get()));
        inventory.setItem(14, new ItemStack(Items.SHIELD));
        updateEquipment();

        this.setTame(false);
    }

    /* Remove consuming goals since we have our own */
    @Override
    protected void addConsumingGoals(int prio) {}

    @Override
    protected void addNPCAI() {
        // reimplement only minimum AI and attack Goals - the rest are added in the register method
        ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0F);
        this.initialiseAttackGoals(getAttackGoalsHolder());
        this.addNPCTargetingAI();
        this.addAttackGoal(2);
    }

    public Inventory getCustomInventory() {
        return inventory;
    }

    @Override
    public ItemStack eat(World world, ItemStack stack) {
        if (stack.isEdible()) {
            this.heal(stack.getItem().getFoodProperties().getNutrition());
        }
        super.eat(world, stack);
        return stack;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(LVL, 1);
        this.entityData.define(CURRENT_XP, 0);
        this.entityData.define(MAX_XP, 1);
        this.entityData.define(KILLS, 0);
        this.entityData.define(FOLLOWING, false);
        this.entityData.define(GUARDING, false);
        this.entityData.define(PATROLLING, false);
        this.entityData.define(ALERT, false);
        this.entityData.define(STATIONARY, false);
        this.entityData.define(BASE_HEALTH, 30);
        this.entityData.define(INVENTORY_OPEN, false);
        this.entityData.define(TMP_LAST_HEALTH, 30f);
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

    public ItemStack checkFood() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (itemstack.isEdible()) {
                return itemstack;
            }
        }
        return ItemStack.EMPTY;
    }

    public void setCurrentXp(int currentXp) {
        this.entityData.set(CURRENT_XP, currentXp);
    }

    public int getCurrentXp() {
        return this.entityData.get(CURRENT_XP);
    }

    public boolean isInventoryOpen() {
        return this.entityData.get(INVENTORY_OPEN);
    }

    public void setMaxXp(int maxXp) {
        this.entityData.set(MAX_XP, maxXp);
    }

    public void setInventoryOpen(boolean isOpen) {
        this.entityData.set(INVENTORY_OPEN, isOpen);
    }

    public int getMaxXp() {
        return this.entityData.get(MAX_XP);
    }

    public void setBaseHealth(int health) {
        this.entityData.set(BASE_HEALTH, health);
    }

    public int getBaseHealth() {
        return this.entityData.get(BASE_HEALTH);
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        System.out.println("Mob Interact Called!");

        ItemStack itemstack = player.getItemInHand(hand);
        if (hand == Hand.MAIN_HAND) {
            if (this.isAlliedTo(player)) {
                if (!this.level.isClientSide()) {
                    this.openGui((ServerPlayerEntity) player);
                }
            }
            return ActionResultType.sidedSuccess(this.level.isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    private void setPatrolling(boolean patrolling) {
        this.entityData.set(PATROLLING, patrolling);
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
        //setStationary(true);
        setInventoryOpen(true);

        // synchronize the equipment slots
        inventory.setItem(9, getItemBySlot(EquipmentSlotType.HEAD));
        inventory.setItem(10, getItemBySlot(EquipmentSlotType.CHEST));
        inventory.setItem(11, getItemBySlot(EquipmentSlotType.LEGS));
        inventory.setItem(12, getItemBySlot(EquipmentSlotType.FEET));
        inventory.setItem(13, getItemBySlot(EquipmentSlotType.MAINHAND));
        inventory.setItem(14, getItemBySlot(EquipmentSlotType.OFFHAND));

        player.containerMenu = new CompanionContainer(
                player.containerCounter, player.inventory, inventory, getId()
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
        this.goalSelector.addGoal(1, new CustomSitGoal(this,this));
        this.goalSelector.addGoal(3, new CustomFollowOwnerGoal(this, this,1.3D, 8.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new EatGoal(this,this));
        this.goalSelector.addGoal(5, new CustomWaterAvoidingRandomWalkingGoal(this,this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(9, new LowHealthGoal(this,this));
        this.targetSelector.addGoal(1, new CustomOwnerHurtByTargetGoal(this,this));
        this.targetSelector.addGoal(2, new CustomOwnerHurtTargetGoal(this,this));
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

        // create temp NonNullList to save inventory to
        NonNullList<ItemStack> items = NonNullList.withSize(15, ItemStack.EMPTY);
        for (int i = 0; i < 15; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) items.set(i, item);
        }
        ItemStackHelper.saveAllItems(tag, items);

        tag.putBoolean("following", this.isFollowing());
        tag.putBoolean("stationary", this.isStationary());
        tag.putInt("mob_kills", this.getMobKills());
        tag.putInt("xp_level", this.getExpLvl());
        tag.putInt("current_xp", this.getCurrentXp());
        tag.putInt("max_xp", this.getMaxXp());
        tag.putInt("base_health", this.getBaseHealth());
        tag.putFloat("tmp_last_health", this.getHealth());
        System.out.println("Saving tmp_last_health = " + this.getHealth());
    }

    private float getTmpLastHealth() {
        return this.entityData.get(TMP_LAST_HEALTH);
    }

    public boolean isFollowing() {
        return this.entityData.get(FOLLOWING);
    }

    public boolean isPatrolling() {
        return this.entityData.get(PATROLLING);
    }

    public boolean isAlert() {
        return this.entityData.get(ALERT);
    }

    public boolean isStationary() {
        return this.entityData.get(STATIONARY);
    }

    public void setStationary(boolean stationary) {
        this.entityData.set(STATIONARY, stationary);
    }

    public void setFollowing(boolean following) {
        this.entityData.set(FOLLOWING, following);
    }

    public void tame(PlayerEntity p_193101_1_) {
        this.setTame(true);
        this.setOwnerUUID(p_193101_1_.getUUID());
        this.setFollowing(true);
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

        NonNullList<ItemStack> items = NonNullList.withSize(15, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, items);
        for (int i = 0; i < 15; i++) {
            ItemStack item = items.get(i);
            if (!item.getItem().equals(ItemStack.EMPTY)) this.inventory.setItem(i, items.get(i));
        }
        updateEquipment();

        if (tag.contains("following")) {
            this.setFollowing(tag.getBoolean("following"));
        }
        if (tag.contains("xp_level")) {
            this.setExpLvl(tag.getInt("xp_level"));
        }
        if (tag.contains("current_xp")) {
            this.setCurrentXp(tag.getInt("current_xp"));
        }
        if (tag.contains("max_xp")) {
            this.setMaxXp(tag.getInt("max_xp"));
        }
        if (tag.contains("mob_kills")) {
            this.setMobKills(tag.getInt("mob_kills"));
        }
        if (tag.contains("base_health")) {
            this.setBaseHealth(tag.getInt("base_health"));
        }
        if (tag.contains("tmp_last_health")) {
            this.entityData.set(TMP_LAST_HEALTH, tag.getFloat("tmp_last_health"));
            this.setHealth(tag.getFloat("tmp_last_health"));
            tmpHealthLoaded = true;
        }

        if (tag.contains("stationary")) {
            this.setStationary(tag.getBoolean("stationary"));
        }
    }

    public void tick() {
        checkStats();
        updateEquipment();

        if (tmpHealthLoaded && !healthUpdateFromTmpHealth) {
            this.setHealth(getTmpLastHealth());
            healthUpdateFromTmpHealth = true;
        }

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

    public void checkStats() {
        if ((int) this.getMaxHealth() != getBaseHealth()) {
            modifyMaxHealth(getBaseHealth() - 30, "Base Health from current level", false);
        }
    }

    public void updateEquipment() {
        setItemSlot(EquipmentSlotType.HEAD, inventory.getItem(9));
        setItemSlot(EquipmentSlotType.CHEST, inventory.getItem(10));
        setItemSlot(EquipmentSlotType.LEGS, inventory.getItem(11));
        setItemSlot(EquipmentSlotType.FEET, inventory.getItem((12)));
        setItemSlot(EquipmentSlotType.MAINHAND, inventory.getItem(13));
        setItemSlot(EquipmentSlotType.OFFHAND, inventory.getItem(14));
    }

    public void modifyMaxHealth(int change, String name, boolean permanent) {
        ModifiableAttributeInstance attributeInstance = this.getAttribute(Attributes.MAX_HEALTH);
        Set<AttributeModifier> modifiers = attributeInstance.getModifiers();
        if (!modifiers.isEmpty()) {
            Iterator<AttributeModifier> iterator = modifiers.iterator();
            while (iterator.hasNext()) {
                AttributeModifier attributeModifier = iterator.next();
                if (attributeModifier != null && attributeModifier.getName().equals(name)) {
                    this.getAttribute(Attributes.MAX_HEALTH).removeModifier(attributeModifier);
                }
            }
        }
        AttributeModifier HEALTH_MODIFIER = new AttributeModifier(name,
                change, AttributeModifier.Operation.ADDITION);
        if (permanent) {
            attributeInstance.addPermanentModifier(HEALTH_MODIFIER);
        } else {
            attributeInstance.addTransientModifier(HEALTH_MODIFIER);
        }
    }

    public void giveExperiencePoints(int points) {
        int newExperience = getCurrentXp() + points;
        if (newExperience >= getMaxXp()) {
            setExpLvl(getExpLvl() + 1);
            int difference = newExperience - getMaxXp();
            setCurrentXp(difference);
            setMaxXp(getMaxXp() + 2);
            setHealth(getHealth() + 2);
            setBaseHealth(getBaseHealth() + 2);
        } else {
            setCurrentXp(newExperience);
        }
    }
}

