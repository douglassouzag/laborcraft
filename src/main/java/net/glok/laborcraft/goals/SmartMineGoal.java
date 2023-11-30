package net.glok.laborcraft.goals;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.helpers.AreaHelper;
import net.glok.laborcraft.helpers.BlockHelper;
import net.glok.laborcraft.helpers.NavigationHelper;
import net.glok.laborcraft.state.StateMachineGoal.StateEnum;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo.Map;

public class SmartMineGoal extends Goal {

  private enum MiningAction {
    MINE,
    FILL,
    FILL_HOLE,
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

  private List<Map.Entry<BlockPos, MiningAction>> actions = new ArrayList<>();

  private final NPCEntity npc;

  private final AreaHelper areaHelper = new AreaHelper();
  private final BlockHelper blockHelper = new BlockHelper();
  private final NavigationHelper navigationHelper = new NavigationHelper();

  private Box lastWorkArea;

  private final Block[] gapBlocks = new Block[] {
    Blocks.AIR,
    Blocks.CAVE_AIR,
    Blocks.WATER,
    Blocks.LAVA,
    Blocks.SEAGRASS,
    Blocks.TALL_SEAGRASS,
    Blocks.GRASS,
    Blocks.KELP_PLANT,
  };

  private Block supportBlock = Blocks.COBBLESTONE_SLAB;

  private BlockPos currentStairPos;

  public SmartMineGoal(NPCEntity npc) {
    this.npc = npc;
  }

  private List<Map.Entry<BlockPos, MiningAction>> setupMiningArea(
    World world,
    Box area
  ) {
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
            actions.add(
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

          actions.add(
            new AbstractMap.SimpleEntry<>(blockPos, MiningAction.MINE)
          );

          if (currentStairPos == null) {
            currentStairPos = blockPos;
          }

          if (blockPos.equals(currentStairPos)) {
            actions.add(
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
    return actions;
  }

  private boolean isBlockAboveHole(World world, BlockPos blockPos) {
    BlockPos blockPosDown = blockPos.down();
    Block block = world.getBlockState(blockPosDown).getBlock();

    return (Arrays.asList(gapBlocks).contains(block));
  }

  private void putBlock(Block block, BlockPos pos) {
    World world = npc.getEntityWorld();
    if (block instanceof SlabBlock) {
      world.setBlockState(
        pos,
        block.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP)
      );
    } else {
      world.setBlockState(pos, block.getDefaultState());
    }
  }

  @Override
  public boolean canStart() {
    return (
      this.npc.isWorkAreaValid() && this.npc.currentState == StateEnum.WORKING
    );
  }

  private int currentActionIndex = 0;

  @Override
  public void tick() {
    if (this.actions == null || this.lastWorkArea != this.npc.workArea) {
      this.actions = setupMiningArea(this.npc.getWorld(), this.npc.workArea);
      this.lastWorkArea = this.npc.workArea;
      this.currentActionIndex = 0;
      return;
    }

    areaHelper.showAreaVisually(this.npc.getWorld(), this.npc.workArea);

    if (currentActionIndex >= actions.size()) return;

    Map.Entry<BlockPos, MiningAction> nextActionEntry = actions.get(
      currentActionIndex
    );
    BlockPos nextCoords = nextActionEntry.getKey();
    MiningAction nextAction = nextActionEntry.getValue();

    navigationHelper.navigateTo(npc, nextCoords);

    if (isBlockAboveHole(this.npc.getWorld(), nextCoords)) {
      putBlock(Blocks.COBBLESTONE, nextCoords.down());

      return;
    }

    if (nextAction == MiningAction.FILL) {
      putBlock(Blocks.COBBLESTONE, nextCoords);
    }
    if (nextAction == MiningAction.BUILD_SUPPORT_BLOCK) {
      putBlock(supportBlock, nextCoords);
    }

    if (nextAction == MiningAction.FILL_HOLE) {
      putBlock(Blocks.COBBLESTONE, nextCoords);
    }

    if (nextAction == MiningAction.MINE) {
      blockHelper.breakBlock(npc.getWorld(), nextCoords, false);
    }

    currentActionIndex++;
  }
}
