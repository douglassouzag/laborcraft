package net.glok.laborcraft.goals;

import java.util.HashMap;
import java.util.Map;
import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.helpers.BlockHelper;
import net.glok.laborcraft.helpers.NavigationHelper;
import net.glok.laborcraft.state.StateMachineGoal.StateEnum;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class LumberGoal extends Goal {

  private NPCEntity npc;

  private final Block[] logBlocks = new Block[] {
    Blocks.OAK_LOG,
    Blocks.SPRUCE_LOG,
    Blocks.BIRCH_LOG,
    Blocks.JUNGLE_LOG,
    Blocks.ACACIA_LOG,
    Blocks.DARK_OAK_LOG,
    Blocks.CHERRY_LOG,
    Blocks.CRIMSON_STEM,
    Blocks.WARPED_STEM,
  };

  private final Map<Block, Block> saplingMap = new HashMap<Block, Block>() {
    {
      put(Blocks.OAK_LOG, Blocks.OAK_SAPLING);
      put(Blocks.SPRUCE_LOG, Blocks.SPRUCE_SAPLING);
      put(Blocks.BIRCH_LOG, Blocks.BIRCH_SAPLING);
      put(Blocks.JUNGLE_LOG, Blocks.JUNGLE_SAPLING);
      put(Blocks.ACACIA_LOG, Blocks.ACACIA_SAPLING);
      put(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_SAPLING);
      put(Blocks.CRIMSON_STEM, Blocks.CRIMSON_FUNGUS);
      put(Blocks.WARPED_STEM, Blocks.WARPED_FUNGUS);
    }
  };

  private NavigationHelper navigationHelper = new NavigationHelper();
  private BlockHelper blockHelper = new BlockHelper();

  public LumberGoal(NPCEntity npc) {
    this.npc = npc;
  }

  private boolean isBlockTouchingGround(World world, BlockPos pos) {
    Block blockBelow = world.getBlockState(pos.down()).getBlock();
    return blockBelow == Blocks.DIRT || blockBelow == Blocks.GRASS_BLOCK;
  }

  private Block getSaplingFromLog(Block log) {
    return this.saplingMap.get(log);
  }

  private boolean anyTreesToCut(World world, Box area) {
    BlockPos minPos = new BlockPos(
      (int) area.minX,
      (int) area.minY,
      (int) area.minZ
    );
    BlockPos maxPos = new BlockPos(
      (int) area.maxX,
      (int) area.maxY,
      (int) area.maxZ
    );

    for (BlockPos pos : BlockPos.iterate(minPos, maxPos)) {
      Block block = world.getBlockState(pos).getBlock();
      for (Block log : this.logBlocks) {
        if (block == log) {
          return true;
        }
      }
    }
    return false;
  }

  private BlockPos findNearestLogToCut(World world, Box area, BlockPos pos) {
    BlockPos nearestLog = null;
    double nearestDistance = Double.MAX_VALUE;

    for (double y = area.minY; y <= area.maxY; y++) {
      for (double x = area.minX; x <= area.maxX; x++) {
        for (double z = area.minZ; z <= area.maxZ; z++) {
          BlockPos blockPos = new BlockPos((int) x, (int) y, (int) z);
          Block block = world.getBlockState(blockPos).getBlock();
          for (Block log : this.logBlocks) {
            if (block == log) {
              double distance = blockPos.getSquaredDistance(pos);
              if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestLog = blockPos;
              }
            }
          }
        }
      }
    }

    return nearestLog;
  }

  @Override
  public boolean canStart() {
    if (!this.npc.isWorkAreaValid()) return false;

    return (
      this.npc.currentState == StateEnum.WORKING &&
      anyTreesToCut(this.npc.getWorld(), this.npc.workArea)
    );
  }

  @Override
  public void tick() {
    BlockPos nextLog = findNearestLogToCut(
      this.npc.getWorld(),
      this.npc.workArea,
      this.npc.getBlockPos()
    );

    if (nextLog == null) return;
    Block nextLogBlock = this.npc.getWorld().getBlockState(nextLog).getBlock();
    boolean isLogTouchingGround = isBlockTouchingGround(
      this.npc.getWorld(),
      nextLog
    );
    navigationHelper.navigateTo(this.npc, nextLog);

    if (navigationHelper.isStopped(this.npc)) {
      Block sapling = getSaplingFromLog(nextLogBlock);

      blockHelper.breakBlockProgressivelyWithEntity(npc, nextLog, true);

      Block currentBlock =
        this.npc.getWorld().getBlockState(nextLog).getBlock();
      if (
        (currentBlock == Blocks.AIR || currentBlock == Blocks.CAVE_AIR) &&
        isLogTouchingGround
      ) {
        blockHelper.putBlockWithEntity(npc, nextLog, sapling);
      }
    }
  }
}
