package net.glok.laborcraft.helpers;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public class PlayerInteractListener {

  public void init() {
    UseEntityCallback.EVENT.register(
      (player, world, hand, entity, hitResult) -> {
        if (
          player instanceof PlayerEntity &&
          hand != null &&
          hand.equals(player.getActiveHand())
        ) {
          if (entity instanceof LivingEntity) {
            // Right-click interaction with an entity
            handleInteraction((PlayerEntity) player, (LivingEntity) entity);
          }
        }
        return ActionResult.PASS;
      }
    );
  }

  private void handleInteraction(PlayerEntity player, LivingEntity entity) {
    // Process the interaction here
    ClientInfoManager.setLastInteractedEntity(entity);
    // You can perform additional actions based on the interaction
  }
}
