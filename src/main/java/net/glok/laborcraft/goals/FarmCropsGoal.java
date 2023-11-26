package net.glok.laborcraft.goals;

import java.util.Arrays;
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

  private final Block[] cropsToLookFor = new Block[] {
    Blocks.WHEAT,
    Blocks.POTATOES,
    Blocks.CARROTS,
    Blocks.BEETROOTS,
  };

  private Block lastCropHarvested;

  public FarmCropsGoal(NPCEntity npc) {
    this.npc = npc;
  }

  public boolean isThereAnyEmptyFarmland(Box workArea) {
    World world = npc.getEntityWorld();
    for (double x = workArea.minX; x <= workArea.maxX; x++) {
      for (double y = workArea.minY; y <= workArea.maxY; y++) {
        for (double z = workArea.minZ; z <= workArea.maxZ; z++) {
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
    for (double x = workArea.minX; x <= workArea.maxX; x++) {
      for (double y = workArea.minY; y <= workArea.maxY; y++) {
        for (double z = workArea.minZ; z <= workArea.maxZ; z++) {
          int xPos = (int) x;
          int yPos = (int) y;
          int zPos = (int) z;
          BlockPos pos = new BlockPos(xPos, yPos, zPos);
          Block block = world.getBlockState(pos).getBlock();
          if (Arrays.asList(this.cropsToLookFor).contains(block)) {
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
    BlockPos npcPos = this.npc.getBlockPos();
    BlockPos closestPos = null;
    double closestDistance = Double.MAX_VALUE;

    for (double x = npc.workArea.minX; x <= npc.workArea.maxX; x++) {
      for (double y = npc.workArea.minY; y <= npc.workArea.maxY; y++) {
        for (double z = npc.workArea.minZ; z <= npc.workArea.maxZ; z++) {
          int xPos = (int) x;
          int yPos = (int) y;
          int zPos = (int) z;
          BlockPos pos = new BlockPos(xPos, yPos, zPos);
          Block block = world.getBlockState(pos).getBlock();
          if (
            block == Blocks.FARMLAND && world.getBlockState(pos.up()).isAir()
          ) {
            double distance = pos.getSquaredDistance(npcPos);
            if (distance < closestDistance) {
              closestDistance = distance;
              closestPos = pos;
            }
          }
        }
      }
    }

    return closestPos;
  }

  public BlockPos findNearestMatureCrop() {
    World world = npc.getEntityWorld();
    BlockPos playerPos = npc.getBlockPos();
    BlockPos closestPos = null;
    double closestDistance = Double.MAX_VALUE;

    for (double x = npc.workArea.minX; x <= npc.workArea.maxX; x++) {
      for (double y = npc.workArea.minY; y <= npc.workArea.maxY; y++) {
        for (double z = npc.workArea.minZ; z <= npc.workArea.maxZ; z++) {
          int xPos = (int) x;
          int yPos = (int) y;
          int zPos = (int) z;
          BlockPos pos = new BlockPos(xPos, yPos, zPos);
          Block block = world.getBlockState(pos).getBlock();
          if (Arrays.asList(this.cropsToLookFor).contains(block)) {
            if (((CropBlock) block).isMature(world.getBlockState(pos))) {
              double distance = playerPos.getSquaredDistance(xPos, yPos, zPos);
              if (distance < closestDistance) {
                closestDistance = distance;
                closestPos = pos;
              }
            }
          }
        }
      }
    }
    return closestPos;
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
          world.setBlockState(
            pos.up(),
            getRightBlock(itemStack.getItem()).getDefaultState()
          );
          itemStack.decrement(1);
          return;
        }
      }
    }
  }

  public void plantEspecificSeedFromInventory(
    BlockPos pos,
    Item especificSeed
  ) {
    World world = npc.getEntityWorld();

    for (ItemStack itemStack : npc.getItems()) {
      if (itemStack.getItem() == especificSeed) {
        this.npc.swingHand(Hand.MAIN_HAND);
        this.npc.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
        world.setBlockState(
          pos.up(),
          getRightBlock(especificSeed).getDefaultState()
        );
        itemStack.decrement(1);
        return;
      }
    }
  }

  public void harvestCrop(BlockPos pos) {
    World world = npc.getEntityWorld();
    Block block = world.getBlockState(pos).getBlock();
    if (Arrays.asList(this.cropsToLookFor).contains(block)) {
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

  public Item getAppropriateSeed(Block lastCropHarvested) {
    if (lastCropHarvested == Blocks.WHEAT) {
      return Items.WHEAT_SEEDS;
    } else if (lastCropHarvested == Blocks.POTATOES) {
      return Items.POTATO;
    } else if (lastCropHarvested == Blocks.CARROTS) {
      return Items.CARROT;
    } else if (lastCropHarvested == Blocks.BEETROOTS) {
      return Items.BEETROOT_SEEDS;
    } else {
      return null;
    }
  }

  private Block getRightBlock(Item plantedSeed) {
    if (plantedSeed == Items.WHEAT_SEEDS) {
      return Blocks.WHEAT;
    } else if (plantedSeed == Items.POTATO) {
      return Blocks.POTATOES;
    } else if (plantedSeed == Items.CARROT) {
      return Blocks.CARROTS;
    } else if (plantedSeed == Items.BEETROOT_SEEDS) {
      return Blocks.BEETROOTS;
    } else {
      return null;
    }
  }

  @Override
  public void tick() {
    BlockPos emptyFarmLand = findNearestEmptyFarmLand();
    if (emptyFarmLand != null && haveAnySeedsInInventory()) {
      goTo(emptyFarmLand);
      if (isNearEnough(emptyFarmLand)) {
        if (lastCropHarvested != null) {
          Item seed = getAppropriateSeed(lastCropHarvested);
          plantEspecificSeedFromInventory(emptyFarmLand, seed);
        } else {
          plantAnySeedsFromInventory(emptyFarmLand);
        }
      }
    } else {
      BlockPos matureCrop = findNearestMatureCrop();
      if (matureCrop != null) {
        goTo(matureCrop);
        if (isNearEnough(matureCrop)) {
          lastCropHarvested =
            npc.getEntityWorld().getBlockState(matureCrop).getBlock();
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
