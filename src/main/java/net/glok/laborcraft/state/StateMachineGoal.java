package net.glok.laborcraft.state;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.helpers.InventoryHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class StateMachineGoal extends Goal {

  private final InventoryHelper inventoryHelper = new InventoryHelper();

  public enum StateEnum {
    IDLE,
    WORKING,
    SLEEPING,
    DEPOSITING,
    COLLECTING,
    FIGHTING,
    FLEEING,
    FOLLOWING,
    TALKING,
    PRAYING,
  }

  private final Map<StateEnum, Set<StateEnum>> transitions;
  private final NPCEntity npc;

  public StateMachineGoal(NPCEntity npc) {
    this.transitions = new HashMap<>();
    this.npc = npc;
    this.transitions.put(
        StateEnum.IDLE,
        EnumSet.of(
          StateEnum.WORKING,
          StateEnum.SLEEPING,
          StateEnum.DEPOSITING,
          StateEnum.FIGHTING,
          StateEnum.FLEEING,
          StateEnum.FOLLOWING,
          StateEnum.TALKING,
          StateEnum.PRAYING
        )
      );
    this.transitions.put(
        StateEnum.WORKING,
        EnumSet.of(
          StateEnum.SLEEPING,
          StateEnum.DEPOSITING,
          StateEnum.FOLLOWING,
          StateEnum.IDLE
        )
      );
    this.transitions.put(
        StateEnum.DEPOSITING,
        EnumSet.of(
          StateEnum.WORKING,
          StateEnum.FOLLOWING,
          StateEnum.SLEEPING,
          StateEnum.IDLE
        )
      );

    this.transitions.put(
        StateEnum.SLEEPING,
        EnumSet.of(StateEnum.IDLE, StateEnum.FOLLOWING, StateEnum.WORKING)
      );
    this.transitions.put(
        StateEnum.FIGHTING,
        EnumSet.of(StateEnum.IDLE, StateEnum.FOLLOWING)
      );
    this.transitions.put(
        StateEnum.FLEEING,
        EnumSet.of(StateEnum.IDLE, StateEnum.FOLLOWING)
      );
    this.transitions.put(StateEnum.FOLLOWING, EnumSet.of(StateEnum.IDLE));

    this.transitions.put(
        StateEnum.TALKING,
        EnumSet.of(StateEnum.IDLE, StateEnum.FOLLOWING)
      );
    this.transitions.put(
        StateEnum.PRAYING,
        EnumSet.of(StateEnum.IDLE, StateEnum.FOLLOWING)
      );
  }

  public boolean transitionTo(StateEnum newState) {
    if (this.npc.currentState == newState) return false;

    Set<StateEnum> validTransitions =
      this.transitions.get(this.npc.currentState);
    if (validTransitions != null && validTransitions.contains(newState)) {
      System.out.println(
        "Transitioning from " + this.npc.currentState + " to " + newState
      );
      this.npc.currentState = newState;
      return true;
    }
    return false;
  }

  @Override
  public boolean canStart() {
    return true;
  }

  @Override
  public void tick() {
    DefaultedList<ItemStack> inventory = this.npc.getItems();
    long time = this.npc.getWorld().getTimeOfDay() % 24000;

    if (inventoryHelper.isInventoryFull(inventory)) {
      transitionTo(StateEnum.DEPOSITING);
    }

    if (this.npc.owner != null) {
      transitionTo(StateEnum.FOLLOWING);
    }
    if (
      time >= 0 &&
      time < 12000 &&
      this.npc.isWorkAreaValid() &&
      this.npc.haveWorkTool
    ) {
      transitionTo(StateEnum.WORKING);
    } else if (time >= 12000 && time < 13000) {
      transitionTo(StateEnum.DEPOSITING);
    } else if (time >= 13000 && time < 23000 && this.npc.isBedPositionValid()) {
      transitionTo(StateEnum.SLEEPING);
    }
  }
}
