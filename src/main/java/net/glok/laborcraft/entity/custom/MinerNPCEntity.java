package net.glok.laborcraft.entity.custom;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.glok.laborcraft.Laborcraft;
import net.glok.laborcraft.goals.CollectItemsGoal;
import net.glok.laborcraft.goals.DepositItemsInChestGoal;
import net.glok.laborcraft.goals.ManageToolsGoal;
import net.glok.laborcraft.goals.SmartMineGoal;
import net.glok.laborcraft.goals.SmartMineGoal.MiningAction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class MinerNPCEntity extends WorkerNPCEntity {

  public List<Map.Entry<BlockPos, MiningAction>> actions = new ArrayList<>();

  public Box lastWorkArea;

  public MinerNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(
      entityType,
      world,
      new Identifier(Laborcraft.MOD_ID, "textures/entity/profession/miner.png")
    );
  }

  @Override
  protected void initGoals() {
    super.initGoals();
    this.goalSelector.add(
        0,
        new ManageToolsGoal(
          this,
          new Item[] {
            Items.WOODEN_PICKAXE,
            Items.STONE_PICKAXE,
            Items.IRON_PICKAXE,
            Items.GOLDEN_PICKAXE,
            Items.DIAMOND_PICKAXE,
            Items.NETHERITE_PICKAXE,
            Items.WOODEN_SHOVEL,
            Items.STONE_SHOVEL,
            Items.IRON_SHOVEL,
            Items.GOLDEN_SHOVEL,
            Items.DIAMOND_SHOVEL,
            Items.NETHERITE_SHOVEL,
          },
          true
        )
      );
    this.goalSelector.add(
        3,
        new CollectItemsGoal(
          this,
          new Item[] {
            Items.COAL,
            Items.RAW_IRON,
            Items.RAW_GOLD,
            Items.RAW_COPPER,
            Items.REDSTONE,
            Items.LAPIS_LAZULI,
            Items.EMERALD,
            Items.DIAMOND,
            Items.COBBLESTONE,
            Items.DEEPSLATE,
            Items.DIRT,
            Items.SAND,
            Items.CLAY,
            Items.TUFF,
            Items.GRAVEL,
            Items.FLINT,
            Items.SANDSTONE,
            Items.GRANITE,
            Items.DIORITE,
            Items.ANDESITE,
          }
        )
      );
    this.goalSelector.add(3, new SmartMineGoal(this));

    this.goalSelector.add(
        8,
        new DepositItemsInChestGoal(
          this,
          new Item[] {
            Items.COAL,
            Items.RAW_IRON,
            Items.RAW_GOLD,
            Items.RAW_COPPER,
            Items.REDSTONE,
            Items.LAPIS_LAZULI,
            Items.EMERALD,
            Items.DIAMOND,
            Items.COBBLESTONE,
            Items.DEEPSLATE,
            Items.DIRT,
            Items.SAND,
            Items.CLAY,
            Items.TUFF,
            Items.GRAVEL,
            Items.FLINT,
            Items.SANDSTONE,
            Items.GRANITE,
            Items.DIORITE,
            Items.ANDESITE,
          }
        )
      );
  }

  public NbtCompound saveActionsToNBT(
    List<Map.Entry<BlockPos, MiningAction>> actions
  ) {
    NbtCompound compound = new NbtCompound();
    NbtList actionsList = new NbtList();
    for (Map.Entry<BlockPos, MiningAction> entry : actions) {
      NbtCompound actionCompound = new NbtCompound();
      actionCompound.putInt("x", entry.getKey().getX());
      actionCompound.putInt("y", entry.getKey().getY());
      actionCompound.putInt("z", entry.getKey().getZ());
      actionCompound.putString("action", entry.getValue().name());
      actionsList.add(actionCompound);
    }
    compound.put("actions", actionsList);
    return compound;
  }

  public List<Map.Entry<BlockPos, MiningAction>> loadActionsFromNBT(
    NbtCompound compound
  ) {
    List<Map.Entry<BlockPos, MiningAction>> actionsToReturn = new ArrayList<>();
    NbtList actionsList = compound.getList("actions", 10); // 10 is the ID for NbtCompound
    for (int i = 0; i < actionsList.size(); i++) {
      NbtCompound actionCompound = actionsList.getCompound(i);
      BlockPos pos = new BlockPos(
        actionCompound.getInt("x"),
        actionCompound.getInt("y"),
        actionCompound.getInt("z")
      );
      MiningAction action = MiningAction.valueOf(
        actionCompound.getString("action")
      );
      actionsToReturn.add(new AbstractMap.SimpleEntry<>(pos, action));
    }

    return actionsToReturn;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    if (this.actions.size() > 0) {
      nbt.put("actions", saveActionsToNBT(actions));
    }
    return super.writeNbt(nbt);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    if (nbt.contains("actions")) {
      this.actions = loadActionsFromNBT(nbt.getCompound("actions"));
    }
  }

  @Override
  public void tick() {
    super.tick();

    if (this.workArea != this.lastWorkArea && this.lastWorkArea != null) {
      this.actions.clear();
      this.lastWorkArea = this.workArea;
    }
  }
}
