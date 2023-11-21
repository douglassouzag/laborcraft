package net.glok.laborcraft.goals;

import java.util.Arrays;
import net.glok.laborcraft.entity.custom.DefaultWorkerEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class BreakBlocksGoal extends Goal {

  protected final DefaultWorkerEntity mob;
  protected final EntityNavigation entityNavigation;
  protected final World world;
  protected final DefaultedList<ItemStack> inventory;
  protected final boolean replantTrees;

  protected final Block[] blocksToBreak;

  protected final Item[] workTools;

  public static Block[] naturalGroundBlocks = new Block[] {
    Blocks.GRASS_BLOCK,
    Blocks.DIRT,
    Blocks.COARSE_DIRT,
    Blocks.PODZOL,
    Blocks.MYCELIUM,
  };

  public BreakBlocksGoal(
    DefaultWorkerEntity mob,
    Block[] blocksToBreak,
    Item[] workTools,
    boolean replantTrees
  ) {
    this.mob = mob;
    this.entityNavigation = mob.getNavigation();
    this.world = mob.getWorld();
    this.inventory = mob.getItems();
    this.blocksToBreak = blocksToBreak;
    this.workTools = workTools;
    this.replantTrees = replantTrees;
  }

  public boolean hasWorkTool() {
    if (workTools.length == 0) return true;

    for (ItemStack item : inventory) {
      for (Item tool : workTools) {
        if (item.getItem() == tool && !isToolLowDurability(item)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isToolLowDurability(ItemStack item) {
    return item.getDamage() > item.getMaxDamage() - 10;
  }

  public void unequipCurrentTool() {
    mob.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
  }

  public Item equipBestWorkTool() {
    Item bestWorkTool = null;
    int bestDurability = 0;

    for (ItemStack item : inventory) {
      if (isWorkTool(item)) {
        int durability = item.getMaxDamage() - item.getDamage();
        if (bestWorkTool == null || durability > bestDurability) {
          bestWorkTool = item.getItem();
          bestDurability = durability;
        }
      }
    }

    if (bestWorkTool != null) {
      mob.equipStack(EquipmentSlot.MAINHAND, new ItemStack(bestWorkTool));
    } else {
      mob.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }

    return bestWorkTool;
  }

  private boolean isWorkTool(ItemStack item) {
    for (Item tool : workTools) {
      if (item.getItem() == tool) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canStart() {
    return hasWorkTool();
  }

  public BlockPos searchForNearestBlockToBreakInRange(double range) {
    BlockPos pos = this.mob.getBlockPos();
    BlockPos nearestBlockToBreak = null;
    double nearestDistance = Double.MAX_VALUE;
    for (
      int x = (int) pos.getX() - (int) range;
      x < pos.getX() + (int) range;
      x++
    ) {
      for (
        int y = (int) pos.getY() - (int) range;
        y < pos.getY() + (int) range;
        y++
      ) {
        for (
          int z = (int) pos.getZ() - (int) range;
          z < pos.getZ() + (int) range;
          z++
        ) {
          BlockPos blockPos = new BlockPos(x, y, z);
          Block block = this.world.getBlockState(blockPos).getBlock();

          if (
            Arrays.asList(blocksToBreak).contains(block) &&
            isBlockInWorkChunk(blockPos)
          ) {
            double distance = pos.getManhattanDistance(blockPos);
            if (distance < nearestDistance) {
              nearestDistance = distance;
              nearestBlockToBreak = blockPos;
            }
          }
        }
      }
    }
    return nearestBlockToBreak;
  }

  private boolean isBlockInWorkChunk(BlockPos blockPos) {
    ChunkPos chunkPos = new ChunkPos(blockPos);
    return chunkPos.equals(this.mob.workChunk);
  }

  private void goTo(BlockPos blockPos) {
    entityNavigation.startMovingTo(
      blockPos.getX(),
      blockPos.getY(),
      blockPos.getZ(),
      0.5f
    );
  }

  private boolean isBlockTouchingGround(BlockPos blockPos) {
    Block blockBelow = this.world.getBlockState(blockPos.down()).getBlock();
    return (Arrays.asList(naturalGroundBlocks).contains(blockBelow));
  }

  public boolean isNearEnough(BlockPos blockPos) {
    return this.mob.getBlockPos().getManhattanDistance(blockPos) < 16;
  }

  public boolean isMoving() {
    return this.entityNavigation.isFollowingPath();
  }

  public void damageWorkTool() {
    ItemStack item = mob.getEquippedStack(EquipmentSlot.MAINHAND);
    for (ItemStack itemStack : inventory) {
      if (itemStack.getItem() == item.getItem()) {
        itemStack.damage(
          1,
          mob,
          entity -> {
            entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
          }
        );
      }
    }
  }

  public Block getSapling(BlockPos blockPos) {
    Block block = this.world.getBlockState(blockPos).getBlock();
    if (block == Blocks.OAK_LOG) {
      return Blocks.OAK_SAPLING;
    } else if (block == Blocks.SPRUCE_LOG) {
      return Blocks.SPRUCE_SAPLING;
    } else if (block == Blocks.BIRCH_LOG) {
      return Blocks.BIRCH_SAPLING;
    } else if (block == Blocks.JUNGLE_LOG) {
      return Blocks.JUNGLE_SAPLING;
    } else if (block == Blocks.ACACIA_LOG) {
      return Blocks.ACACIA_SAPLING;
    } else if (block == Blocks.DARK_OAK_LOG) {
      return Blocks.DARK_OAK_SAPLING;
    } else if (block == Blocks.CHERRY_LOG) {
      return Blocks.CHERRY_SAPLING;
    } else {
      return null;
    }
  }

  public void replantTree(BlockPos blockPos, Block sappling) {
    if (sappling != null) {
      this.world.setBlockState(blockPos, sappling.getDefaultState());
    }
  }

  public void breakBlockWithTool(BlockPos blockPos) {
    boolean wasTouchingGround = isBlockTouchingGround(blockPos);
    Block sappling = getSapling(blockPos);

    this.mob.getLookControl()
      .lookAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());

    if (!this.mob.handSwinging) {
      this.mob.workerSwingHand(Hand.MAIN_HAND, true);
    }

    this.world.breakBlock(blockPos, true, mob);

    if (wasTouchingGround) {
      replantTree(blockPos, sappling);
    }
    if (workTools.length != 0) {
      damageWorkTool();
    }
  }

  @Override
  public void tick() {
    if (this.mob.boss != null) return;
    equipBestWorkTool();

    BlockPos blockToBreakPos = searchForNearestBlockToBreakInRange(16f);

    if (blockToBreakPos != null) {
      goTo(blockToBreakPos);

      if (isNearEnough(blockToBreakPos) && !isMoving()) {
        breakBlockWithTool(blockToBreakPos);
      }
    }
  }

  @Override
  public void stop() {
    unequipCurrentTool();
  }
}
