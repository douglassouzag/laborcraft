package net.glok.laborcraft.goals;

import java.util.AbstractMap;
import java.util.Arrays;
import net.glok.laborcraft.entity.custom.MinerNPCEntity;
import net.glok.laborcraft.helpers.AreaHelper;
import net.glok.laborcraft.helpers.BlockHelper;
import net.glok.laborcraft.helpers.NavigationHelper;
import net.glok.laborcraft.state.StateMachineGoal.StateEnum;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo.Map;

public class SmartMineGoal extends Goal {

  public enum MiningAction {
    MINE,
    FILL,
    BUILD_SUPPORT_BLOCK,
  }

  private enum Directions {
    NORTH,
    SOUTH,
    EAST,
    WEST,
  }

  private Directions currentDirection = Directions.NORTH;

  private final Block[] invalidBlocks = new Block[] {
    Blocks.BEDROCK,
    Blocks.SPAWNER,
  };

  private final MinerNPCEntity npc;

  private final AreaHelper areaHelper = new AreaHelper();
  private final BlockHelper blockHelper = new BlockHelper();
  private final NavigationHelper navigationHelper = new NavigationHelper();

  private final Block[] gapBlocks = new Block[] {
    Blocks.AIR,
    Blocks.CAVE_AIR,
    Blocks.WATER,
    Blocks.LAVA,
    Blocks.SEAGRASS,
    Blocks.TALL_SEAGRASS,
    Blocks.GRASS,
    Blocks.KELP_PLANT,
    Blocks.AMETHYST_CLUSTER,
  };

  private Block supportBlock = Blocks.COBBLESTONE_SLAB;
  private Block fillHoleBlock = Blocks.COBBLESTONE;

  private BlockPos currentStairPos;

  public SmartMineGoal(MinerNPCEntity npc) {
    this.npc = npc;
  }

  private void setupMiningArea(World world, Box area) {
    Box outerArea = new Box(
      area.minX - 1,
      -59,
      area.minZ - 1,
      area.maxX + 1,
      area.maxY - 10,
      area.maxZ + 1
    );

    Box innerArea = new Box(
      area.minX,
      -59,
      area.minZ,
      area.maxX,
      area.maxY - 10,
      area.maxZ
    );

    for (int y = (int) outerArea.maxY; y >= outerArea.minY; y--) {
      for (int x = (int) outerArea.minX; x <= outerArea.maxX; x++) {
        for (int z = (int) outerArea.minZ; z <= outerArea.maxZ; z++) {
          BlockPos blockPos = new BlockPos(x, y, z);
          boolean isInEdge = areaHelper.isInEdge(outerArea, blockPos);

          if (!isInEdge) continue;

          Block block = world.getBlockState(blockPos).getBlock();

          if (Arrays.asList(invalidBlocks).contains(block)) continue;

          if (Arrays.asList(gapBlocks).contains(block)) {
            this.npc.actions.add(
                new AbstractMap.SimpleEntry<>(blockPos, MiningAction.FILL)
              );
          }
          continue;
        }
      }

      for (int x = (int) outerArea.minX; x <= outerArea.maxX; x++) {
        for (int z = (int) outerArea.minZ; z <= outerArea.maxZ; z++) {
          BlockPos blockPos = new BlockPos(x, y, z);
          boolean isInEdge = areaHelper.isInEdge(outerArea, blockPos);

          if (isInEdge) continue;

          Block block = world.getBlockState(blockPos).getBlock();
          if (Arrays.asList(invalidBlocks).contains(block)) continue;

          this.npc.actions.add(
              new AbstractMap.SimpleEntry<>(blockPos, MiningAction.MINE)
            );

          if (currentStairPos == null) {
            currentStairPos = blockPos;
          }

          if (blockPos.equals(currentStairPos)) {
            this.npc.actions.add(
                new AbstractMap.SimpleEntry<>(
                  currentStairPos,
                  MiningAction.BUILD_SUPPORT_BLOCK
                )
              );

            if (currentDirection == Directions.NORTH) {
              currentStairPos = currentStairPos.east().down();
            } else if (currentDirection == Directions.EAST) {
              currentStairPos = currentStairPos.south().down();
            } else if (currentDirection == Directions.SOUTH) {
              currentStairPos = currentStairPos.west().down();
            } else if (currentDirection == Directions.WEST) {
              currentStairPos = currentStairPos.north().down();
            }

            if (areaHelper.isCornerBlock(innerArea, currentStairPos)) {
              currentDirection =
                currentDirection == Directions.NORTH
                  ? Directions.EAST
                  : currentDirection == Directions.EAST
                    ? Directions.SOUTH
                    : currentDirection == Directions.SOUTH
                      ? Directions.WEST
                      : Directions.NORTH;
            }
          }

          continue;
        }
      }
    }
  }

  private boolean isBlockAboveHole(World world, BlockPos blockPos) {
    BlockPos blockPosDown = blockPos.down();
    Block block = world.getBlockState(blockPosDown).getBlock();

    return (Arrays.asList(gapBlocks).contains(block));
  }

  @Override
  public boolean canStart() {
    return (
      this.npc.isWorkAreaValid() &&
      this.npc.currentState == StateEnum.WORKING &&
      this.npc.haveWorkTool
    );
  }

  @Override
  public void tick() {
    if (this.npc.lastWorkArea == null) {
      this.npc.lastWorkArea = this.npc.workArea;
    }

    if (this.npc.actions.isEmpty()) {
      setupMiningArea(this.npc.getWorld(), this.npc.workArea);
      return;
    }

    Map.Entry<BlockPos, MiningAction> nextActionEntry = this.npc.actions.get(0);
    BlockPos nextCoords = nextActionEntry.getKey();
    MiningAction nextAction = nextActionEntry.getValue();

    navigationHelper.navigateTo(npc, nextCoords);

    if (navigationHelper.isNearEnough(npc, nextCoords, 20f)) {
      if (nextAction == MiningAction.FILL) {
        blockHelper.putBlockWithEntity(npc, nextCoords, fillHoleBlock);
        this.npc.actions.remove(0);
      }
      if (nextAction == MiningAction.BUILD_SUPPORT_BLOCK) {
        blockHelper.putBlockWithEntity(npc, nextCoords, supportBlock);
        this.npc.actions.remove(0);
      }

      if (nextAction == MiningAction.MINE) {
        if (isBlockAboveHole(this.npc.getWorld(), nextCoords)) {
          blockHelper.putBlockWithEntity(npc, nextCoords.down(), fillHoleBlock);
        }

        blockHelper.breakBlockProgressivelyWithEntity(npc, nextCoords, true);
        Block currentBlock =
          this.npc.getWorld().getBlockState(nextCoords).getBlock();

        if (currentBlock == Blocks.AIR || currentBlock == Blocks.CAVE_AIR) {
          this.npc.actions.remove(0);
        }
      }

      if (this.npc.actions.isEmpty()) {
        this.npc.workArea = null;
      }
    }
  }
}
