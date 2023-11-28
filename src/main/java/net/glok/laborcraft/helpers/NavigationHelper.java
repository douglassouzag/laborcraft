package net.glok.laborcraft.helpers;

import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;

public class NavigationHelper {

  public void navigateTo(PathAwareEntity entity, BlockPos pos) {
    entity
      .getNavigation()
      .startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5f);
  }

  public void stopNavigation(PathAwareEntity entity) {
    entity.getNavigation().stop();
  }

  public boolean isNearEnough(
    PathAwareEntity entity,
    BlockPos pos,
    double distance
  ) {
    return (
      entity.getPos().squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <
      distance
    );
  }

  public boolean isBesideBlock(PathAwareEntity entity, BlockPos pos) {
    BlockPos entityPos = entity.getBlockPos();

    int dx = Math.abs(entityPos.getX() - pos.getX());
    int dy = Math.abs(entityPos.getY() - pos.getY());
    int dz = Math.abs(entityPos.getZ() - pos.getZ());

    return (dx <= 1 && dy == 0 && dz <= 1) && (dx + dz > 0);
  }
}
