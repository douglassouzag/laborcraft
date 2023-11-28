package net.glok.laborcraft.helpers;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class AreaHelper {

  public boolean isInside(Box area, BlockPos pos) {
    int minX = (int) Math.floor(area.minX);
    int minY = (int) Math.floor(area.minY);
    int minZ = (int) Math.floor(area.minZ);
    int maxX = (int) Math.floor(area.maxX);
    int maxY = (int) Math.floor(area.maxY);
    int maxZ = (int) Math.floor(area.maxZ);

    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          BlockPos currentPos = new BlockPos(x, y, z);
          if (currentPos.equals(pos)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public BlockPos findClosestBlock(
    World world,
    Box area,
    BlockPos pos,
    Block[] targetBlocks,
    boolean liquidSource
  ) {
    BlockPos closestPos = null;
    double closestDistance = Double.MAX_VALUE;

    int minX = Math.min((int) area.minX, (int) area.maxX);
    int minY = Math.min((int) area.minY, (int) area.maxY);
    int minZ = Math.min((int) area.minZ, (int) area.maxZ);
    int maxX = Math.max((int) area.minX, (int) area.maxX);
    int maxY = Math.max((int) area.minY, (int) area.maxY);
    int maxZ = Math.max((int) area.minZ, (int) area.maxZ);

    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          BlockPos currentPos = new BlockPos(x, y, z);
          Block currentBlock = world.getBlockState(currentPos).getBlock();
          if (liquidSource) {
            if (
              Arrays.asList(targetBlocks).contains(currentBlock) &&
              world.getFluidState(currentPos).isStill()
            ) {
              double distance = pos.getSquaredDistance(currentPos);
              if (distance < closestDistance) {
                closestDistance = distance;
                closestPos = currentPos;
              }
            }
          } else {
            if (Arrays.asList(targetBlocks).contains(currentBlock)) {
              double distance = pos.getSquaredDistance(currentPos);
              if (distance < closestDistance) {
                closestDistance = distance;
                closestPos = currentPos;
              }
            }
          }
        }
      }
    }

    return closestPos;
  }

  public boolean isInEdge(Box area, BlockPos pos) {
    int minX = (int) Math.floor(area.minX);
    int minZ = (int) Math.floor(area.minZ);
    int maxX = (int) Math.floor(area.maxX);
    int maxZ = (int) Math.floor(area.maxZ);

    if (pos.getX() == minX || pos.getX() == maxX) {
      return true;
    }

    if (pos.getZ() == minZ || pos.getZ() == maxZ) {
      return true;
    }
    return false;
  }

  private void applyVisualEffect(World world, BlockPos pos) {
    world.addParticle(
      ParticleTypes.FLAME,
      pos.getX(),
      pos.getY(),
      pos.getZ(),
      1,
      1,
      1
    );
  }

  public void showAreaVisually(World world, Box area) {
    int minX = Math.min((int) area.minX, (int) area.maxX);
    int minY = Math.min((int) area.minY, (int) area.maxY);
    int minZ = Math.min((int) area.minZ, (int) area.maxZ);
    int maxX = Math.max((int) area.minX, (int) area.maxX);
    int maxY = Math.max((int) area.minY, (int) area.maxY);
    int maxZ = Math.max((int) area.minZ, (int) area.maxZ);

    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          BlockPos pos = new BlockPos(x, y, z);
          if (isInEdge(area, pos)) {
            applyVisualEffect(world, pos);
          }
        }
      }
    }
  }
}
