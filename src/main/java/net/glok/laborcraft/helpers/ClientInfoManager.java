package net.glok.laborcraft.helpers;

import net.minecraft.entity.LivingEntity;

public class ClientInfoManager {

  private static LivingEntity lastInteractedEntity;

  public static LivingEntity getLastInteractedEntity() {
    return lastInteractedEntity;
  }

  public static void setLastInteractedEntity(LivingEntity entity) {
    lastInteractedEntity = entity;
  }
}
