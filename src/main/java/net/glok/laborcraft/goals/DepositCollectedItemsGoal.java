package net.glok.laborcraft.goals;

import net.minecraft.entity.ai.goal.Goal;

public class DepositCollectedItemsGoal extends Goal {

  @Override
  public boolean canStart() {
    return true;
  }
}
