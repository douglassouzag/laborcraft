package net.glok.laborcraft.goals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.helpers.AreaHelper;
import net.glok.laborcraft.helpers.BlockHelper;
import net.glok.laborcraft.state.StateMachineGoal.StateEnum;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo.Map;

public class SmartMineGoal extends Goal {

  private enum MiningAction {
    MINE,
    FILL,
    REMOVE,
    PLACE,
  }

  private final Block[] gapBlocks = new Block[] {
    Blocks.AIR,
    Blocks.CAVE_AIR,
    Blocks.WATER,
    Blocks.LAVA,
  };

  private final Block[] invalidBlocks = new Block[] {
    Blocks.BEDROCK,
    Blocks.SPAWNER,
  };

  private LinkedHashMap<BlockPos, MiningAction> miningMap;
  private ArrayList<BlockPos> liquidsToRemove = new ArrayList<BlockPos>();

  private final NPCEntity npc;
  private final AreaHelper areaHelper = new AreaHelper();
  private final BlockHelper blockHelper = new BlockHelper();

  private Box lastWorkArea;

  private Block supportBlock = Blocks.COBBLESTONE_SLAB;

  public SmartMineGoal(NPCEntity npc) {
    this.npc = npc;
  }

  //StairCase
  private boolean canPlaceBlock(BlockPos blockPos) {
    Block blockToBeReplaced =
      this.npc.getWorld().getBlockState(blockPos).getBlock();

    return (
      Arrays.asList(gapBlocks).contains(blockToBeReplaced) ||
      blockToBeReplaced == Blocks.COBBLESTONE
    );
  }

  private BlockPos calculateStairBlockPos(
    BlockPos blockBelow,
    Box workArea,
    int tries
  ) {
    int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
    if (tries < 0 || tries >= directions.length) {
      return null;
    }

    BlockPos newBlockPos = new BlockPos(
      blockBelow.getX() + directions[tries][0],
      blockBelow.getY(),
      blockBelow.getZ() + directions[tries][1]
    );

    if (!areaHelper.isInside(workArea, newBlockPos)) return null;

    if (
      this.npc.getWorld().getBlockState(newBlockPos.up().up()).getBlock() ==
      supportBlock
    ) {
      return null;
    }

    if (
      !blockHelper.haveSolidBlockOnSide(this.npc.getWorld(), newBlockPos)
    ) return null;

    if (!canPlaceBlock(newBlockPos)) {
      return null;
    }

    if (!areaHelper.isInEdge(workArea, newBlockPos)) {
      return null;
    }

    return newBlockPos;
  }

  private BlockPos findNextStairCaseBlock(Box workArea) {
    workArea =
      new Box(
        workArea.minX,
        -59,
        workArea.minZ,
        workArea.maxX,
        workArea.maxY,
        workArea.maxZ
      );

    BlockPos nextStairCasePos = findLowestStairBlockPos(workArea);

    if (nextStairCasePos != null) {
      BlockPos blockBelow = nextStairCasePos.down();

      for (int i = 0; i < 4; i++) {
        BlockPos possibleNestBlock = calculateStairBlockPos(
          blockBelow,
          workArea,
          i
        );

        if (possibleNestBlock != null) {
          return possibleNestBlock;
        }
      }
    } else {
      nextStairCasePos = findFirstStairBlockPos(workArea);
      if (nextStairCasePos != null) {
        return nextStairCasePos;
      }
    }

    return null;
  }

  private BlockPos findFirstStairBlockPos(Box workArea) {
    World world = this.npc.getWorld();
    double northEastCornerX = workArea.maxX;
    double northEastCornerz = workArea.maxZ;

    for (double y = workArea.maxY; y >= workArea.minY; y--) {
      BlockPos pos = new BlockPos(
        (int) northEastCornerX,
        (int) y,
        (int) northEastCornerz
      );

      if (blockHelper.haveSolidBlockOnSide(world, pos)) {
        return pos;
      }
    }

    return null;
  }

  public BlockPos findLowestStairBlockPos(Box workArea) {
    World world = npc.getEntityWorld();
    for (double y = workArea.minY; y <= workArea.maxY; y++) {
      for (double x = workArea.minX; x <= workArea.maxX; x++) {
        for (double z = workArea.minZ; z <= workArea.maxZ; z++) {
          BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
          Block block = world.getBlockState(pos).getBlock();
          if (
            block == supportBlock &&
            blockHelper.haveSolidBlockOnSide(world, pos)
          ) {
            return pos;
          }
        }
      }
    }
    return null;
  }

  //

  private MiningAction getMiningAction(Block block, boolean isInEdge) {
    if (isInEdge) {
      return MiningAction.FILL;
    }
    return MiningAction.MINE;
  }

  private void setMiningMap(World world, Box workArea) {
    Box outerWorkArea = new Box(
      workArea.minX - 1,
      -59,
      workArea.minZ - 1,
      workArea.maxX + 1,
      workArea.maxY - 10,
      workArea.maxZ + 1
    );

    for (int y = (int) outerWorkArea.maxY; y >= outerWorkArea.minY; y--) {
      for (int x = (int) outerWorkArea.minX; x <= outerWorkArea.maxX; x++) {
        for (int z = (int) outerWorkArea.minZ; z <= outerWorkArea.maxZ; z++) {
          BlockPos pos = new BlockPos(x, y, z);
          Block block = world.getBlockState(pos).getBlock();
          Block upBlock = world.getBlockState(pos.up()).getBlock();
          boolean isInEdge = areaHelper.isInEdge(outerWorkArea, pos);
          boolean isLiquidToRemove = false;

          if (!isInEdge && blockHelper.isLiquidAndSource(world, pos)) {
            liquidsToRemove.add(pos);
            isLiquidToRemove = true;
          }

          if (
            !isInEdge &&
            (block == Blocks.AIR || block == Blocks.CAVE_AIR) &&
            (upBlock != Blocks.AIR || upBlock != Blocks.CAVE_AIR)
          ) {
            liquidsToRemove.add(pos);
            isLiquidToRemove = true;
          }

          if (Arrays.asList(invalidBlocks).contains(block)) continue;

          if (isInEdge && !Arrays.asList(gapBlocks).contains(block)) continue;

          if (
            !isInEdge &&
            Arrays.asList(gapBlocks).contains(block) &&
            !isLiquidToRemove
          ) continue;

          miningMap.put(pos, getMiningAction(block, isInEdge));
        }
      }
    }
  }

  private void removeFromMiningMap(BlockPos pos) {
    miningMap.remove(pos);
  }

  private BlockPos getNextCoords() {
    if (!miningMap.isEmpty()) {
      Map.Entry<BlockPos, MiningAction> entry = miningMap
        .entrySet()
        .iterator()
        .next();
      BlockPos coord = entry.getKey();
      return coord;
    }
    return null;
  }

  private BlockPos getNextLiquidToRemove() {
    if (liquidsToRemove.isEmpty()) return null;
    return liquidsToRemove.get(0);
  }

  private void removeLiquidToRemove(BlockPos pos) {
    liquidsToRemove.remove(pos);
  }

  private void putBlock(Block block, BlockPos pos) {
    this.npc.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
    this.npc.swingHand(Hand.MAIN_HAND);
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

  @Override
  public void tick() {
    if (this.miningMap == null || this.lastWorkArea != this.npc.workArea) {
      this.miningMap = new LinkedHashMap<BlockPos, MiningAction>();
      setMiningMap(this.npc.getWorld(), this.npc.workArea);
      this.lastWorkArea = this.npc.workArea;
      return;
    }

    BlockPos nextStairCasePos = findNextStairCaseBlock(this.npc.workArea);

    if (nextStairCasePos != null) {
      putBlock(supportBlock, nextStairCasePos);

      return;
    }

    BlockPos nextCoords = getNextCoords();
    if (nextCoords == null) return;

    int currentYLevel = nextCoords.getY();

    BlockPos liquidToRemove = getNextLiquidToRemove();
    if (liquidToRemove != null) {
      int liquidToRemoveYLevel = liquidToRemove.getY();
      if (
        liquidToRemoveYLevel <= currentYLevel &&
        currentYLevel - liquidToRemoveYLevel <= 1
      ) {
        putBlock(Blocks.COBBLESTONE, liquidToRemove);
        removeLiquidToRemove(liquidToRemove);
        return;
      }
    }

    MiningAction nextAction = this.miningMap.get(nextCoords);

    if (nextAction == MiningAction.FILL) {
      putBlock(Blocks.COBBLESTONE, nextCoords);
    }
    if (nextAction == MiningAction.MINE) {
      blockHelper.breakBlock(this.npc.getWorld(), nextCoords, false);
      removeFromMiningMap(nextCoords);
    }
    removeFromMiningMap(nextCoords);
  }
}
