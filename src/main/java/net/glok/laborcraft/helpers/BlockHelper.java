package net.glok.laborcraft.helpers;

import java.util.Arrays;
import net.glok.laborcraft.entity.custom.NPCEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHelper {

  private int lastBreakProgress = 0;

  public static Block[] liquidBlocks = { Blocks.WATER, Blocks.LAVA };

  public void putBlock(World world, BlockPos pos, Block block) {
    if (block instanceof SlabBlock) {
      world.setBlockState(
        pos,
        block.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP)
      );
    } else {
      world.setBlockState(pos, block.getDefaultState());
    }
  }

  public void putBlockWithEntity(
    LivingEntity entity,
    BlockPos pos,
    Block block
  ) {
    World world = entity.getEntityWorld();
    entity.swingHand(Hand.MAIN_HAND);
    this.putBlock(world, pos, block);
  }

  public void breakBlock(World world, BlockPos pos, boolean dropItems) {
    world.breakBlock(pos, dropItems);
  }

  public void breakBlockWithEntity(
    LivingEntity entity,
    BlockPos pos,
    boolean dropItems
  ) {
    World world = entity.getEntityWorld();
    entity.swingHand(Hand.MAIN_HAND);
    this.breakBlock(world, pos, dropItems);
  }

  public void breakBlockProgressivelyWithTool(
    World world,
    BlockPos pos,
    boolean dropItems,
    ItemStack tool
  ) {
    Block block = world.getBlockState(pos).getBlock();
    BlockState blockState = block.getDefaultState();

    float miningSpeed =
      tool.getMiningSpeedMultiplier(blockState) *
      (1 - ((float) tool.getDamage() / tool.getMaxDamage()));

    int breakingProgress = (int) (
      this.lastBreakProgress + Math.ceil((0.5 * miningSpeed))
    );

    if (breakingProgress >= 10) breakingProgress = 10;

    world.setBlockBreakingInfo(0, pos, breakingProgress);

    if (lastBreakProgress >= 10) {
      this.lastBreakProgress = 0;
      this.breakBlock(world, pos, dropItems);
      tool.damage(1, world.getRandom(), null);
    } else {
      this.lastBreakProgress = breakingProgress;
    }
  }

  public void breakBlockProgressivelyWithEntity(
    NPCEntity entity,
    BlockPos pos,
    boolean dropItems
  ) {
    World world = entity.getEntityWorld();
    ItemStack tool = entity.getMainHandStack();
    entity.swingHand(Hand.MAIN_HAND);
    Block block = world.getBlockState(pos).getBlock();
    entity.blockToBreak = block;
    this.breakBlockProgressivelyWithTool(world, pos, dropItems, tool);
  }

  public boolean isBlockSolid(World world, BlockPos pos) {
    return world.getBlockState(pos).isSolidBlock(world, pos);
  }

  public boolean isLiquid(World world, BlockPos pos) {
    return Arrays
      .asList(liquidBlocks)
      .contains(world.getBlockState(pos).getBlock());
  }

  public boolean isLiquidAndSource(World world, BlockPos pos) {
    return (
      this.isLiquid(world, pos) &&
      world.getBlockState(pos).getFluidState().isStill()
    );
  }

  public boolean haveSolidBlockOnSide(World world, BlockPos pos) {
    return (
      isBlockSolid(world, pos.north()) ||
      isBlockSolid(world, pos.south()) ||
      isBlockSolid(world, pos.east()) ||
      isBlockSolid(world, pos.west())
    );
  }
}
