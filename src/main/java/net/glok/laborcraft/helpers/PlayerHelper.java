package net.glok.laborcraft.helpers;

import net.glok.laborcraft.entity.custom.NPCEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class PlayerHelper {

  public boolean isPlayerHandSwinging(PlayerEntity player) {
    return player.handSwingTicks == -1;
  }

  public BlockPos getLookingPosition(PlayerEntity player, double maxDistance) {
    Vec3d playerPosition = player.getCameraPosVec(1.0F);
    Vec3d playerDirection = player.getRotationVec(1.0F);
    Vec3d lookTargetPosition = playerPosition.add(
      playerDirection.x * maxDistance,
      playerDirection.y * maxDistance,
      playerDirection.z * maxDistance
    );

    HitResult raycastHitResult = player
      .getWorld()
      .raycast(
        new RaycastContext(
          playerPosition,
          lookTargetPosition,
          RaycastContext.ShapeType.OUTLINE,
          RaycastContext.FluidHandling.NONE,
          player
        )
      );

    if (raycastHitResult.getType() == HitResult.Type.BLOCK) {
      return ((BlockHitResult) raycastHitResult).getBlockPos();
    } else {
      BlockPos.Mutable mutablePos = new BlockPos.Mutable(
        lookTargetPosition.x,
        lookTargetPosition.y,
        lookTargetPosition.z
      );
      return mutablePos.toImmutable();
    }
  }

  public NPCEntity getClosestNPCEntity(PlayerEntity player) {
    World world = player.getEntityWorld();
    NPCEntity closestNPC = null;
    double closestDistance = Double.MAX_VALUE;

    Box searchBox = player.getBoundingBox().expand(16);

    for (NPCEntity npcEntity : world.getEntitiesByClass(
      NPCEntity.class,
      searchBox,
      entity -> true
    )) {
      double distance = player.squaredDistanceTo(npcEntity);

      if (distance < closestDistance) {
        closestNPC = npcEntity;
        closestDistance = distance;
      }
    }

    return closestNPC;
  }
}
