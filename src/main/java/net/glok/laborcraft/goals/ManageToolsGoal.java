package net.glok.laborcraft.goals;

import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.state.StateMachineGoal.StateEnum;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ManageToolsGoal extends Goal {

  private final NPCEntity npc;
  private final Item[] canUseTools;
  private boolean needToHaveTool = true;

  public enum ToolEnum {
    PICKAXE,
    SHOVEL,
    AXE,
    HOE,
  }

  public ManageToolsGoal(
    NPCEntity npc,
    Item[] canUseTools,
    boolean needToHaveTool
  ) {
    this.npc = npc;
    this.canUseTools = canUseTools;
  }

  private ItemStack getBestWorkToolBasedOnBlock(
    DefaultedList<ItemStack> inventory,
    Item[] tools,
    Block block
  ) {
    ItemStack bestTool = ItemStack.EMPTY;
    float bestScore = -1.0F;
    int durabilityThreshold = 10;

    for (Item tool : tools) {
      for (ItemStack stack : inventory) {
        if (stack.getItem().equals(tool)) {
          float efficiency = tool.getMiningSpeedMultiplier(
            stack,
            block.getDefaultState()
          );
          int durability = stack.getMaxDamage() - stack.getDamage();
          float score = efficiency * durability;

          if (score > bestScore && durability > durabilityThreshold) {
            bestTool = stack;
            bestScore = score;
          }

          break;
        }
      }
    }

    return bestTool;
  }

  @Override
  public boolean canStart() {
    return (this.canUseTools.length > 0);
  }

  @Override
  public void tick() {
    Block blockToBreak = this.npc.blockToBreak;
    if (blockToBreak == null) return;

    ItemStack bestTool =
      this.getBestWorkToolBasedOnBlock(
          this.npc.getItems(),
          this.canUseTools,
          blockToBreak
        );

    if (bestTool != ItemStack.EMPTY) {
      this.npc.haveWorkTool = true;

      this.npc.equipStack(EquipmentSlot.MAINHAND, bestTool);
    } else {
      if (needToHaveTool) {
        this.npc.haveWorkTool = false;
        this.npc.currentState = StateEnum.IDLE;
      }
    }
  }
}
