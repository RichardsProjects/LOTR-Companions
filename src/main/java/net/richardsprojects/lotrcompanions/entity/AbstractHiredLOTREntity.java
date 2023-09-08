package net.richardsprojects.lotrcompanions.entity;

import lotr.common.entity.npc.GondorSoldierEntity;
import lotr.common.entity.npc.ManEntity;
import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.data.NPCFoodPool;
import lotr.common.entity.npc.data.NPCFoodPools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;
import net.richardsprojects.lotrcompanions.core.PacketHandler;
import net.richardsprojects.lotrcompanions.entity.ai.*;
import net.richardsprojects.lotrcompanions.networking.OpenInventoryPacket;

import javax.annotation.Nullable;
import java.util.*;

public class AbstractHiredLOTREntity extends GondorSoldierEntity {

    protected AbstractHiredLOTREntity(EntityType<? extends GondorSoldierEntity> type, World w) {
        super(type, w);
        this.setTame(false);
        this.setExpLvl(1);

        ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);

        for (int i = 0; i < CompanionData.alertMobs.length; i++) {
            alertMobGoals.add(new NearestAttackableTargetGoal(this, CompanionData.alertMobs[i], false));
        }
        for (int i = 0; i < CompanionData.huntMobs.length; i++) {
            huntMobGoals.add(new NearestAttackableTargetGoal(this, CompanionData.huntMobs[i], false));
        }
    }


    @Override
    public boolean useSmallArmsModel() {
        return false;
    }

    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (hand == Hand.MAIN_HAND) {
            if (!this.isTame() && !this.level.isClientSide()) {
                // TODO: temporarily allow taming with cooked chicken
                if (itemstack.getItem().equals(Items.COOKED_CHICKEN)) {
                    this.tame(player);
                    player.sendMessage(new TranslationTextComponent("chat.type.text", this.getDisplayName(),
                            new StringTextComponent("Thanks!")), this.getUUID());
                    player.sendMessage(new StringTextComponent("Companion added"), this.getUUID());
                    setPatrolPos(null);
                    setPatrolling(false);
                    setFollowing(true);
                    setPatrolRadius(4);
                    patrolGoal.radius = 4;
                    moveBackGoal.radius = 4;
                }
            } else {
                if (this.isAlliedTo(player)) {
                    if(player.isShiftKeyDown()) {
                        if (!this.level.isClientSide()) {
                            // TODO: reimplement stay
                        }
                    } else {
                        if (!this.level.isClientSide()) {
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

    public void openGui(ServerPlayerEntity player) {
        if (player.containerMenu != player.inventoryMenu) {
            player.closeContainer();
        }
        player.nextContainerCounter();
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OpenInventoryPacket(
                player.containerCounter, this.inventory.getContainerSize(), this.getId()));
        player.containerMenu = new CompanionContainer(player.containerCounter, player.inventory, this.inventory);
        player.containerMenu.addSlotListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.containerMenu));
    }

    protected static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(AbstractHiredLOTREntity.class, DataSerializers.BYTE);
    protected static final DataParameter<Optional<UUID>> DATA_OWNERUUID_ID = EntityDataManager.defineId(AbstractHiredLOTREntity.class, DataSerializers.OPTIONAL_UUID);
    private static final DataParameter<Integer> BASE_HEALTH = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.INT);
    private static final DataParameter<Integer> EXP_LVL = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.INT);
    private static final DataParameter<Boolean> EATING = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ALERT = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HUNTING = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> PATROLLING = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GUARDING = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> STATIONERY = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<BlockPos>> PATROL_POS = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.OPTIONAL_BLOCK_POS);
    private static final DataParameter<Integer> PATROL_RADIUS = EntityDataManager.defineId(AbstractHiredLOTREntity.class,
            DataSerializers.INT);

    public Inventory inventory = new Inventory(27);
    public EquipmentSlotType[] armorTypes = new EquipmentSlotType[]{EquipmentSlotType.FEET, EquipmentSlotType.LEGS,
            EquipmentSlotType.CHEST, EquipmentSlotType.HEAD};
    public List<NearestAttackableTargetGoal> alertMobGoals = new ArrayList<>();
    public List<NearestAttackableTargetGoal> huntMobGoals = new ArrayList<>();
    public PatrolGoal patrolGoal;
    public MoveBackToPatrolGoal moveBackGoal;
    public int experienceLevel;
    public int totalExperience;
    public float experienceProgress;
    private int lastLevelUpTime;

    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        if (this.getOwnerUUID() != null) {
            tag.putUUID("Owner", this.getOwnerUUID());
        }

        tag.put("inventory", this.inventory.createTag());
        tag.putBoolean("Alert", this.isAlert());
        tag.putBoolean("Hunting", this.isHunting());
        tag.putBoolean("Patrolling", this.isPatrolling());
        tag.putBoolean("Following", this.isFollowing());
        tag.putBoolean("Guarding", this.isGuarding());
        tag.putBoolean("Stationery", this.isStationery());
        tag.putInt("radius", this.getPatrolRadius());
        tag.putInt("baseHealth", this.getBaseHealth());
        tag.putFloat("XpP", this.experienceProgress);
        tag.putInt("XpLevel", this.experienceLevel);
        tag.putInt("XpTotal", this.totalExperience);

        if (this.getPatrolPos() != null) {
            int[] patrolPos = {this.getPatrolPos().getX(), this.getPatrolPos().getY(), this.getPatrolPos().getZ()};
            tag.putIntArray("patrol_pos", patrolPos);
        }
    }

    public void setExpLvl(int lvl) {
        this.entityData.set(EXP_LVL, lvl);
    }

    public int getExpLvl() {
        return this.entityData.get(EXP_LVL);
    }

    public void setFollowing(boolean following) {
        this.entityData.set(FOLLOWING, following);
    }

    public void setGuarding(boolean guarding) {
        this.entityData.set(GUARDING, guarding);
    }

    @Nullable
    public void setPatrolPos(BlockPos position) { this.entityData.set(PATROL_POS, Optional.ofNullable(position)); }

    @Nullable
    public BlockPos getPatrolPos() { return this.entityData.get(PATROL_POS).orElse(null); }

    public boolean isAlert() {
        return this.entityData.get(ALERT);
    }

    public boolean isHunting() {
        return this.entityData.get(HUNTING);
    }

    public boolean isPatrolling() {
        return this.entityData.get(PATROLLING);
    }

    public boolean isGuarding() {
        return this.entityData.get(GUARDING);
    }

    public boolean isStationery() {
        return this.entityData.get(STATIONERY);
    }

    public boolean isFollowing() {
        return this.entityData.get(FOLLOWING);
    }

    private int getPatrolRadius() {
        return this.entityData.get(PATROL_RADIUS);
    }

    private int getBaseHealth() {
        return this.entityData.get(BASE_HEALTH);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new EatGoal(this));
        this.goalSelector.addGoal(1, new CustomSitGoal(this));
        this.goalSelector.addGoal(2, new AvoidCreeperGoal(this, CreeperEntity.class, 10.0F, 1.5D, 1.5D));
        this.goalSelector.addGoal(3, new MoveBackToGuardGoal(this));
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

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return GondorSoldierEntity.regAttrs()
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(EATING, false);
        this.entityData.define(ALERT, false);
        this.entityData.define(HUNTING, true);
        this.entityData.define(PATROLLING, false);
        this.entityData.define(FOLLOWING, false);
        this.entityData.define(GUARDING, false);
        this.entityData.define(STATIONERY, false);
        this.entityData.define(PATROL_POS, Optional.empty());
        this.entityData.define(PATROL_RADIUS, 10);
        // TODO: test if this can be overridden by the Knight
        this.entityData.define(BASE_HEALTH, 30);
        this.entityData.define(EXP_LVL, 0);
    }

    public void setBaseHealth(int health) {
        this.entityData.set(BASE_HEALTH, health);
    }

    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn,
                                           SpawnReason reason, @Nullable ILivingEntityData spawnDataIn,
                                           @Nullable CompoundNBT dataTag) {
        int baseHealth = LOTRCompanions.BASE_HEALTH  + CompanionData.getHealthModifier();
        modifyMaxHealth(baseHealth - 20, "companion base health", true);
        this.setHealth(this.getMaxHealth());
        setBaseHealth(baseHealth);
        setEating(false);
        setCustomName(new StringTextComponent("Test"));
        setPatrolPos(this.blockPosition());
        setPatrolling(true);
        setPatrolRadius(15);
        patrolGoal = new PatrolGoal(this, 60, getPatrolRadius());
        moveBackGoal = new MoveBackToPatrolGoal(this, getPatrolRadius());
        this.goalSelector.addGoal(3, moveBackGoal);
        this.goalSelector.addGoal(3, patrolGoal);

        // set armor
        for (int i = 0; i < 4; i++) {
            EquipmentSlotType armorType = armorTypes[i];
            ItemStack itemstack = CompanionData.getSpawnArmor(armorType);
            if (!itemstack.isEmpty()) {
                this.inventory.setItem(i, itemstack);
            }
        }
        checkArmor();

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public double getTotalAttackDamage(ItemStack stack) {
        double damage = 0.0;
        double multiplier = 1;
        for (AttributeModifier modifier : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
            switch (modifier.getOperation()) {
                case ADDITION:
                    damage += modifier.getAmount();
                case MULTIPLY_BASE:
                    damage += damage * modifier.getAmount();
                case MULTIPLY_TOTAL:
                    multiplier *= modifier.getAmount();
            }

        }
        double enchantment = EnchantmentHelper.getDamageBonus(stack, CreatureAttribute.UNDEFINED);

        return damage * multiplier + enchantment;
    }
    public void checkArmor() {
        ItemStack head = this.getItemBySlot(EquipmentSlotType.HEAD);
        ItemStack chest = this.getItemBySlot(EquipmentSlotType.CHEST);
        ItemStack legs = this.getItemBySlot(EquipmentSlotType.LEGS);
        ItemStack feet = this.getItemBySlot(EquipmentSlotType.FEET);
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (itemstack.getItem() instanceof ArmorItem) {
                switch (((ArmorItem) itemstack.getItem()).getSlot()) {
                    case HEAD:
                        if (head.isEmpty()) {
                            this.setItemSlot(EquipmentSlotType.HEAD, itemstack);
                        } else {
                            if (((ArmorItem) itemstack.getItem()).getDefense() > ((ArmorItem) head.getItem()).getDefense()) {
                                this.setItemSlot(EquipmentSlotType.HEAD, itemstack);
                            } else if (((ArmorItem) itemstack.getItem()).getMaterial() == ArmorMaterial.NETHERITE && ((ArmorItem) head.getItem()).getMaterial() != ArmorMaterial.NETHERITE) {
                                this.setItemSlot(EquipmentSlotType.HEAD, itemstack);
                            }
                        }
                        break;
                    case CHEST:
                        if (chest.isEmpty()) {
                            this.setItemSlot(EquipmentSlotType.CHEST, itemstack);
                        } else {
                            if (((ArmorItem) itemstack.getItem()).getDefense() > ((ArmorItem) chest.getItem()).getDefense()) {
                                this.setItemSlot(EquipmentSlotType.CHEST, itemstack);
                            } else if (((ArmorItem) itemstack.getItem()).getMaterial() == ArmorMaterial.NETHERITE && ((ArmorItem) chest.getItem()).getMaterial() != ArmorMaterial.NETHERITE) {
                                this.setItemSlot(EquipmentSlotType.CHEST, itemstack);
                            }
                        }
                        break;
                    case LEGS:
                        if (legs.isEmpty()) {
                            this.setItemSlot(EquipmentSlotType.LEGS, itemstack);
                        } else {
                            if (((ArmorItem) itemstack.getItem()).getDefense() > ((ArmorItem) legs.getItem()).getDefense()) {
                                this.setItemSlot(EquipmentSlotType.LEGS, itemstack);
                            } else if (((ArmorItem) itemstack.getItem()).getMaterial() == ArmorMaterial.NETHERITE && ((ArmorItem) legs.getItem()).getMaterial() != ArmorMaterial.NETHERITE) {
                                this.setItemSlot(EquipmentSlotType.LEGS, itemstack);
                            }
                        }
                        break;
                    case FEET:
                        if (feet.isEmpty()) {
                            this.setItemSlot(EquipmentSlotType.FEET, itemstack);
                        } else {
                            if (((ArmorItem) itemstack.getItem()).getDefense() > ((ArmorItem) feet.getItem()).getDefense()) {
                                this.setItemSlot(EquipmentSlotType.FEET, itemstack);
                            } else if (((ArmorItem) itemstack.getItem()).getMaterial() == ArmorMaterial.NETHERITE && ((ArmorItem) feet.getItem()).getMaterial() != ArmorMaterial.NETHERITE) {
                                this.setItemSlot(EquipmentSlotType.FEET, itemstack);
                            }
                        }
                        break;
                }
            }
        }
    }

    public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        UUID uuid;
        if (p_70037_1_.hasUUID("Owner")) {
            uuid = p_70037_1_.getUUID("Owner");
        } else {
            String s = p_70037_1_.getString("Owner");
            uuid = PreYggdrasilConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
                this.setTame(true);
            } catch (Throwable throwable) {
                this.setTame(false);
            }
        }
    }

    public void modifyMaxHealth(int change, String name, boolean permanent) {
        ModifiableAttributeInstance attributeinstance = this.getAttribute(Attributes.MAX_HEALTH);
        Set<AttributeModifier> modifiers = attributeinstance.getModifiers();
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
            attributeinstance.addPermanentModifier(HEALTH_MODIFIER);
        } else {
            attributeinstance.addTransientModifier(HEALTH_MODIFIER);
        }
    }

    public void setEating(boolean eating) {
        this.entityData.set(EATING, eating);
    }

    public boolean getEating() {
        return this.entityData.get(EATING);
    }

    public void setPatrolling(boolean patrolling) {
        this.entityData.set(PATROLLING, patrolling);
    }

    public void setPatrolRadius(int radius) { this.entityData.set(PATROL_RADIUS, radius); }

    @OnlyIn(Dist.CLIENT)
    protected void spawnTamingParticles(boolean p_70908_1_) {
        IParticleData iparticledata = ParticleTypes.HEART;
        if (!p_70908_1_) {
            iparticledata = ParticleTypes.SMOKE;
        }

        for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(iparticledata, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 7) {
            this.spawnTamingParticles(true);
        } else if (p_70103_1_ == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(p_70103_1_);
        }

    }

    public ItemStack checkFood() {
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (itemstack.isEdible()) {
                if ((float)itemstack.getItem().getFoodProperties().getNutrition() + this.getHealth() <= this.getMaxHealth()) {
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
    public void clearTarget() {
        this.setTarget(null);
    }

    public boolean isTame() {
        return (this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
    }

    public void setTame(boolean p_70903_1_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_70903_1_) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 4));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -5));
        }

        this.reassessTameGoals();
    }

    protected void reassessTameGoals() {
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse((UUID)null);
    }

    public void setOwnerUUID(@Nullable UUID p_184754_1_) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(p_184754_1_));
    }

    public void tame(PlayerEntity p_193101_1_) {
        this.setTame(true);
        this.setOwnerUUID(p_193101_1_.getUUID());
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

    public boolean canAttack(LivingEntity p_213336_1_) {
        return this.isOwnedBy(p_213336_1_) ? false : super.canAttack(p_213336_1_);
    }

    public boolean isOwnedBy(LivingEntity p_152114_1_) {
        return p_152114_1_ == this.getOwner();
    }

    public boolean wantsToAttack(LivingEntity p_142018_1_, LivingEntity p_142018_2_) {
        return true;
    }

    public Team getTeam() {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
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

    public void die(DamageSource p_70645_1_) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayerEntity) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), Util.NIL_UUID);
        }

        super.die(p_70645_1_);
    }

    public void setAlert(boolean b) {
        this.entityData.set(ALERT, b);
    }


    public void addAlertGoals() {
        for (NearestAttackableTargetGoal alertMobGoal : alertMobGoals) {
            this.targetSelector.addGoal(4, alertMobGoal);
        }
    }

    public void removeAlertGoals() {
        for (NearestAttackableTargetGoal alertMobGoal : alertMobGoals) {
            this.targetSelector.removeGoal(alertMobGoal);
        }
    }

    public void addHuntingGoals() {
        for (NearestAttackableTargetGoal huntMobGoal : huntMobGoals) {
            this.targetSelector.addGoal(4, huntMobGoal);
        }
    }

    public void removeHuntingGoals() {
        for (NearestAttackableTargetGoal huntMobGoal : huntMobGoals) {
            this.targetSelector.removeGoal(huntMobGoal);
        }
    }

    protected Goal createAttackGoal() {
        return (Goal)new NPCMeleeAttackGoal(this, 1.3D);
    }

    protected void addNPCTargetingAI() {
        addNonAggressiveTargetingGoals();
    }

    protected NPCFoodPool getEatPool() {
        return NPCFoodPools.GONDOR;
    }

    protected NPCFoodPool getDrinkPool() {
        return NPCFoodPools.GONDOR_DRINK;
    }
}
