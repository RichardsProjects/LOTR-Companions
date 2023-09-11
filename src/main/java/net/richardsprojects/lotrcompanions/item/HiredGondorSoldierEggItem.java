package net.richardsprojects.lotrcompanions.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;
import net.richardsprojects.lotrcompanions.entity.LOTRCEntities;

import java.util.Objects;

public class HiredGondorSoldierEggItem extends ForgeSpawnEggItem {
    public HiredGondorSoldierEggItem() {
        super(LOTRCEntities.HIRED_GONDOR_SOLDIER,0xE8AF5A, 0xFF0000,
                new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MISC));
    }

    @Override
    public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
        ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
        RayTraceResult raytraceresult = getPlayerPOVHitResult(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.SOURCE_ONLY);

        System.out.println("Test1");

        if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
            System.out.println("Test2");
            return ActionResult.pass(itemstack);
        } else if (!(p_77659_1_ instanceof ServerWorld)) {
            return ActionResult.success(itemstack);
        } else {
            System.out.println("Test3");
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
            BlockPos blockpos = blockraytraceresult.getBlockPos();
            if (!(p_77659_1_.getBlockState(blockpos).getBlock() instanceof FlowingFluidBlock)) {
                return ActionResult.pass(itemstack);
            } else if (p_77659_1_.mayInteract(p_77659_2_, blockpos) && p_77659_2_.mayUseItemAt(blockpos, blockraytraceresult.getDirection(), itemstack)) {
                EntityType<?> entitytype = this.getType(itemstack.getTag());

                System.out.println("Test4");

                HiredGondorSoldier entity = (HiredGondorSoldier) entitytype.spawn((ServerWorld)p_77659_1_, itemstack, p_77659_2_, blockpos, SpawnReason.SPAWN_EGG, false, false);
                if (entity == null) {
                    return ActionResult.pass(itemstack);
                } else {
                    System.out.println("Test5");
                    entity.tame(p_77659_2_);
                    System.out.println("Test6");

                    if (!p_77659_2_.abilities.instabuild) {
                        itemstack.shrink(1);
                    }

                    p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
                    return ActionResult.consume(itemstack);
                }
            } else {
                return ActionResult.fail(itemstack);
            }
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext p_195939_1_) {
        World world = p_195939_1_.getLevel();
        System.out.println("Test1");
        if (!(world instanceof ServerWorld)) {
            return ActionResultType.SUCCESS;
        } else {
            ItemStack itemstack = p_195939_1_.getItemInHand();
            BlockPos blockpos = p_195939_1_.getClickedPos();
            Direction direction = p_195939_1_.getClickedFace();
            BlockState blockstate = world.getBlockState(blockpos);
            if (blockstate.is(Blocks.SPAWNER)) {
                TileEntity tileentity = world.getBlockEntity(blockpos);
                if (tileentity instanceof MobSpawnerTileEntity) {
                    AbstractSpawner abstractspawner = ((MobSpawnerTileEntity)tileentity).getSpawner();
                    EntityType<?> entitytype1 = this.getType(itemstack.getTag());
                    abstractspawner.setEntityId(entitytype1);
                    tileentity.setChanged();
                    world.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                    itemstack.shrink(1);
                    return ActionResultType.CONSUME;
                }
            }

            System.out.println("Test2");
            BlockPos blockpos1;
            if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            EntityType<?> entitytype = this.getType(itemstack.getTag());
            HiredGondorSoldier entity = (HiredGondorSoldier) entitytype.spawn((ServerWorld)world, itemstack, p_195939_1_.getPlayer(), blockpos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
            if (entity != null) {
                System.out.println("Test3");
                entity.tame(p_195939_1_.getPlayer());
                itemstack.shrink(1);
            }

            return ActionResultType.CONSUME;
        }
    }
}
