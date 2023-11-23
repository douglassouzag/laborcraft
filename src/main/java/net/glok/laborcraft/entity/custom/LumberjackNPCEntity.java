package net.glok.laborcraft.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LumberjackNPCEntity extends NPCEntity {

  public LumberjackNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(entityType, world);
  }

  @Nullable
  @Override
  public ActionResult interactMob(PlayerEntity player, Hand hand) {
    if (player.getStackInHand(hand).getItem() == NPCEntity.followItem) {
      setOwner(player);
      return ActionResult.SUCCESS;
    } else {
      openNPCInventory(player);
    }

    return ActionResult.SUCCESS;
  }
}
