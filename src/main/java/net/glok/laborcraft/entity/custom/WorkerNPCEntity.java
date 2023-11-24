package net.glok.laborcraft.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class WorkerNPCEntity extends PassiveNPCEntity {

  public WorkerNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world,
    Identifier texture
  ) {
    super(entityType, world, texture);
  }

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
