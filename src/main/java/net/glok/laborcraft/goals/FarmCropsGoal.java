package net.glok.laborcraft.goals;

import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.helpers.InventoryHelper;
import net.glok.laborcraft.state.StateMachineGoal.StateEnum;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class FarmCropsGoal extends Goal {

  private final NPCEntity npc;
  private final InventoryHelper inventoryHelper = new InventoryHelper();
  private final Item[] seeds = new Item[] {
    Items.WHEAT_SEEDS,
    Items.POTATO,
    Items.CARROT,
    Items.BEETROOT_SEEDS,
  };

  public FarmCropsGoal(NPCEntity npc) {
    this.npc = npc;
  }

  public boolean isThereAnyEmptyFarmland(Box workArea) {
    World world = npc.getEntityWorld();
    for (double x = workArea.minX; x < workArea.maxX; x++) {
      for (double y = workArea.minY; y < workArea.maxY; y++) {
        for (double z = workArea.minZ; z < workArea.maxZ; z++) {
          int xPos = (int) x;
          int yPos = (int) y;
          int zPos = (int) z;
          BlockPos pos = new BlockPos(xPos, yPos, zPos);
          Block block = world.getBlockState(pos).getBlock();
          if (
            block == Blocks.FARMLAND && world.getBlockState(pos.up()).isAir()
          ) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean isThereAnyCropsMature(Box workArea) {
    World world = npc.getEntityWorld();
    for (double x = workArea.minX; x < workArea.maxX; x++) {
      for (double y = workArea.minY; y < workArea.maxY; y++) {
        for (double z = workArea.minZ; z < workArea.maxZ; z++) {
          int xPos = (int) x;
          int yPos = (int) y;
          int zPos = (int) z;
          BlockPos pos = new BlockPos(xPos, yPos, zPos);
          Block block = world.getBlockState(pos).getBlock();
          if (block == Blocks.WHEAT) {
            if (((CropBlock) block).isMature(world.getBlockState(pos))) {
              return true;
            }
          }
        }
      }
    }

    return false;
  }

  public boolean haveAnySeedsInInventory() {
    for (Item seed : seeds) {
      for (ItemStack itemStack : npc.getItems()) {
        if (itemStack.getItem() == seed) {
          return true;
        }
      }
    }
    return false;
  }

  public BlockPos findNearestEmptyFarmLand() {
    World world = npc.getEntityWorld();
    for (double x = npc.workArea.minX; x < npc.workArea.maxX; x++) {
      for (double y = npc.workArea.minY; y < npc.workArea.maxY; y++) {
        for (double z = npc.workArea.minZ; z < npc.workArea.maxZ; z++) {
          int xPos = (int) x;
          int yPos = (int) y;
          int zPos = (int) z;
          BlockPos pos = new BlockPos(xPos, yPos, zPos);
          Block block = world.getBlockState(pos).getBlock();
          if (
            block == Blocks.FARMLAND && world.getBlockState(pos.up()).isAir()
          ) {
            return pos;
          }
        }
      }
    }
    return null;
  }

  public BlockPos findNearestMatureCrop() {
    World world = npc.getEntityWorld();
    for (double x = npc.workArea.minX; x < npc.workArea.maxX; x++) {
      for (double y = npc.workArea.minY; y < npc.workArea.maxY; y++) {
        for (double z = npc.workArea.minZ; z < npc.workArea.maxZ; z++) {
          int xPos = (int) x;
          int yPos = (int) y;
          int zPos = (int) z;
          BlockPos pos = new BlockPos(xPos, yPos, zPos);
          Block block = world.getBlockState(pos).getBlock();
          if (block == Blocks.WHEAT) {
            if (((CropBlock) block).isMature(world.getBlockState(pos))) {
              return pos;
            }
          }
        }
      }
    }
    return null;
  }

  public void goTo(BlockPos pos) {
    npc.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
  }

  public boolean isNearEnough(BlockPos blockPos) {
    return this.npc.getBlockPos().getManhattanDistance(blockPos) < 3f;
  }

  public void plantAnySeedsFromInventory(BlockPos pos) {
    World world = npc.getEntityWorld();
    for (Item seed : seeds) {
      for (ItemStack itemStack : npc.getItems()) {
        if (itemStack.getItem() == seed) {
          this.npc.swingHand(Hand.MAIN_HAND);
          this.npc.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
          world.setBlockState(pos.up(), Blocks.WHEAT.getDefaultState());
          itemStack.decrement(1);
          return;
        }
      }
    }
  }

  public void harvestCrop(BlockPos pos) {
    World world = npc.getEntityWorld();
    Block block = world.getBlockState(pos).getBlock();
    if (block == Blocks.WHEAT) {
      this.npc.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
      this.npc.swingHand(Hand.MAIN_HAND);

      world.breakBlock(pos, true);
    }
  }

  @Override
  public boolean canStart() {
    return (
      (
        isThereAnyEmptyFarmland(this.npc.workArea) &&
        haveAnySeedsInInventory() &&
        this.npc.isWorkAreaValid() &&
        this.npc.currentState == StateEnum.WORKING
      ) ||
      (
        isThereAnyCropsMature(this.npc.workArea) &&
        !inventoryHelper.isInventoryFull(npc.getItems()) &&
        this.npc.isWorkAreaValid() &&
        this.npc.currentState == StateEnum.WORKING
      )
    );
  }

  @Override
  public void tick() {
    BlockPos emptyFarmLand = findNearestEmptyFarmLand();
    if (emptyFarmLand != null && haveAnySeedsInInventory()) {
      goTo(emptyFarmLand);
      if (isNearEnough(emptyFarmLand)) {
        plantAnySeedsFromInventory(emptyFarmLand);
      }
    } else {
      BlockPos matureCrop = findNearestMatureCrop();
      if (matureCrop != null) {
        goTo(matureCrop);
        if (isNearEnough(matureCrop)) {
          harvestCrop(matureCrop);
        }
      }
    }
  }

  @Override
  public void stop() {
    this.npc.currentState = StateEnum.IDLE;
  }
}
